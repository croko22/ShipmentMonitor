package com.kevinchambi.shipmentmonitor.data.repository

import com.kevinchambi.shipmentmonitor.data.model.*
import com.kevinchambi.shipmentmonitor.data.network.RetrofitClient

class VehicleRepository {
    private val api = RetrofitClient.instance

    suspend fun getVehicles(): Result<ApiResponse<List<Vehicle>>> {
        return try {
            val response = api.getVehicles()
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
