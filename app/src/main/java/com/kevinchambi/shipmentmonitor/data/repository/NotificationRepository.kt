package com.kevinchambi.shipmentmonitor.data.repository

import com.kevinchambi.shipmentmonitor.data.model.*
import com.kevinchambi.shipmentmonitor.data.network.RetrofitClient
import retrofit2.HttpException

class NotificationRepository {
    private val api = RetrofitClient.instance

    suspend fun getNotifications(): Result<List<Notification>> {
        return try {
            val response = api.getNotifications()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener notificaciones"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Error de servidor: ${e.code()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión. Verifica tu acceso a internet."))
        }
    }
}
