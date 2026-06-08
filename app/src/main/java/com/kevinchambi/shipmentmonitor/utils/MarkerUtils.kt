package com.kevinchambi.shipmentmonitor.utils

import android.content.Context
import android.graphics.*
import android.view.View
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
        val markerView = createMarkerView(context, plate, speed, statusColor)
        val bitmap = viewToBitmap(markerView)
        val rotatedBitmap = rotateBitmap(bitmap, angle.toFloat())
        return BitmapDescriptorFactory.fromBitmap(rotatedBitmap)
    }

    private fun createMarkerView(
        context: Context,
        plate: String,
        speed: String,
        statusColor: String
    ): View {
        // We'll use a canvas-based approach for the marker
        // This creates a marker with plate, speed, and colored indicator
        val markerLayout = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(8, 4, 8, 4)
            val bgColor = Color.WHITE
            background = createRoundedRectDrawable(12f, bgColor, 2f, getColorForStatus(statusColor))
        }

        val plateText = android.widget.TextView(context).apply {
            text = plate
            setTextColor(Color.parseColor("#0041BA"))
            textSize = 10f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
        }

        val speedText = android.widget.TextView(context).apply {
            text = speed
            setTextColor(Color.parseColor("#23B4D9"))
            textSize = 9f
            gravity = android.view.Gravity.CENTER
        }

        markerLayout.addView(plateText)
        markerLayout.addView(speedText)

        val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        markerLayout.measure(widthSpec, heightSpec)
        markerLayout.layout(0, 0, markerLayout.measuredWidth, markerLayout.measuredHeight)

        return markerLayout
    }

    private fun createRoundedRectDrawable(
        radius: Float,
        fillColor: Int,
        strokeWidthParam: Float,
        strokeColor: Int
    ): android.graphics.drawable.Drawable {
        return object : android.graphics.drawable.Drawable() {
            override fun draw(canvas: Canvas) {
                val paint = Paint().apply {
                    color = fillColor
                    isAntiAlias = true
                }
                val strokePaint = Paint().apply {
                    color = strokeColor
                    isAntiAlias = true
                    style = Paint.Style.STROKE
                    strokeWidth = strokeWidthParam
                }
                val rect = RectF(0f, 0f, bounds.width().toFloat(), bounds.height().toFloat())
                canvas.drawRoundRect(rect, radius, radius, paint)
                canvas.drawRoundRect(rect, radius, radius, strokePaint)
            }

            override fun setAlpha(alpha: Int) {}
            override fun setColorFilter(colorFilter: ColorFilter?) {}
            override fun getOpacity(): Int = android.graphics.PixelFormat.TRANSLUCENT
        }
    }

    private fun getColorForStatus(status: String): Int {
        return when (status.lowercase()) {
            "green" -> Color.parseColor("#4CAF50")
            "red" -> Color.parseColor("#F00101")
            "yellow" -> Color.parseColor("#FFC107")
            else -> Color.GRAY
        }
    }

    private fun viewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
