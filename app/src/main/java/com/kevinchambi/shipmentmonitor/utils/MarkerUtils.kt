package com.kevinchambi.shipmentmonitor.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.kevinchambi.shipmentmonitor.R

object MarkerUtils {

    fun createCustomMarker(
        context: Context,
        plate: String,
        speed: String,
        statusColor: String,
        angle: Int
    ): BitmapDescriptor {
        val density = context.resources.displayMetrics.density
        val bitmap = createMarkerBitmap(context, plate, speed, statusColor, density, angle)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun getAnchorU(): Float = 0.5f
    fun getAnchorV(): Float = 1.0f

    private fun createMarkerBitmap(
        context: Context,
        plate: String,
        speed: String,
        statusColor: String,
        density: Float,
        angle: Int
    ): Bitmap {
        val d = density * 0.7f // always small
        val statusIntColor = getColorHex(statusColor)
        val isStopped = speed.startsWith("0")

        // Car icon
        val carIcon = getCarIcon(context, statusColor)
        val carSize = (48 * d).toInt().coerceAtLeast(8)
        val scaledCar = Bitmap.createScaledBitmap(carIcon, carSize, carSize, true)
        val diagonal = (carSize * 1.42).toInt() + 4
        val rotatedCar = rotateBitmapWithPadding(scaledCar, angle.toFloat(), diagonal)

        // Paints
        val platePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 12f * d
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            color = Color.parseColor("#1A1A1A")
            textAlign = Paint.Align.CENTER
        }
        val speedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 10f * d
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            color = if (isStopped) Color.parseColor("#999999") else statusIntColor
            textAlign = Paint.Align.CENTER
        }
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = statusIntColor
            style = Paint.Style.STROKE
            strokeWidth = 2f * d
        }
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#33000000")
            style = Paint.Style.FILL
        }
        val triPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = statusIntColor
            style = Paint.Style.FILL
        }

        // Pill sizing
        val padH = 12f * d
        val padV = 5f * d
        val corner = 14f * d
        val plateW = platePaint.measureText(plate)
        val speedW = speedPaint.measureText(speed)
        val pillW = maxOf(plateW, speedW) + padH * 2
        val pillH = platePaint.textSize + speedPaint.textSize + padV * 3

        // Triangle connector
        val triH = 6f * d
        val triW = 10f * d

        // Canvas
        val totalW = maxOf(rotatedCar.width.toFloat(), pillW) + 4f * d
        val totalH = rotatedCar.height + pillH + triH + 2f * d
        val cx = totalW / 2f

        val bitmap = Bitmap.createBitmap(totalW.toInt(), totalH.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Car icon (rotated)
        canvas.drawBitmap(rotatedCar, cx - rotatedCar.width / 2f, 0f, null)

        // 2. Triangle connector (points up, connects car bottom to pill top)
        val triTop = rotatedCar.height.toFloat()
        val triPath = Path().apply {
            moveTo(cx, triTop)
            lineTo(cx - triW / 2f, triTop + triH)
            lineTo(cx + triW / 2f, triTop + triH)
            close()
        }
        canvas.drawPath(triPath, triPaint)

        // 3. Pill
        val pillTop = triTop + triH
        val pillLeft = cx - pillW / 2f
        val pillRect = RectF(pillLeft, pillTop, pillLeft + pillW, pillTop + pillH)

        // Manual shadow (no BlurMaskFilter)
        canvas.drawRoundRect(
            RectF(pillRect.left + 1.5f * d, pillRect.top + 1.5f * d,
                pillRect.right + 1.5f * d, pillRect.bottom + 1.5f * d),
            corner, corner, shadowPaint
        )

        // Pill background + border
        canvas.drawRoundRect(pillRect, corner, corner, bgPaint)
        canvas.drawRoundRect(pillRect, corner, corner, borderPaint)

        // 4. Plate text
        val plateY = pillTop + padV + platePaint.textSize - platePaint.descent()
        canvas.drawText(plate, cx, plateY, platePaint)

        // 5. Speed text
        val speedY = plateY + speedPaint.textSize + padV * 0.5f
        canvas.drawText(speed, cx, speedY, speedPaint)

        return bitmap
    }

    private fun getCarIcon(context: Context, statusColor: String): Bitmap {
        val drawable = when (statusColor.lowercase()) {
            "green" -> ContextCompat.getDrawable(context, R.drawable.car_green)
            "red" -> ContextCompat.getDrawable(context, R.drawable.car_red)
            else -> ContextCompat.getDrawable(context, R.drawable.car_green)
        }
        return (drawable as BitmapDrawable).bitmap
    }

    private fun getColorHex(statusColor: String): Int {
        return when (statusColor.lowercase()) {
            "green" -> Color.parseColor("#4CAF50")
            "red" -> Color.parseColor("#F44336")
            "yellow" -> Color.parseColor("#FFC107")
            "orange" -> Color.parseColor("#FF9800")
            else -> Color.parseColor("#4CAF50")
        }
    }

    private fun rotateBitmapWithPadding(source: Bitmap, angle: Float, size: Int): Bitmap {
        val result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val matrix = Matrix().apply {
            postTranslate((size - source.width) / 2f, (size - source.height) / 2f)
            postRotate(angle, size / 2f, size / 2f)
        }
        canvas.drawBitmap(source, matrix, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isFilterBitmap = true
        })
        return result
    }
}