package com.kevinchambi.shipmentmonitor.data.repository

import com.kevinchambi.shipmentmonitor.data.model.*
import com.kevinchambi.shipmentmonitor.data.network.RetrofitClient

class NotificationRepository {
    private val api = RetrofitClient.instance

    suspend fun getNotifications(): Result<ApiResponse<List<Notification>>> {
        return try {
            val response = api.getNotifications()
            if (response.success) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
