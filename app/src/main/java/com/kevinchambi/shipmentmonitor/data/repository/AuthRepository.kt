package com.kevinchambi.shipmentmonitor.data.repository

import com.kevinchambi.shipmentmonitor.data.model.*
import com.kevinchambi.shipmentmonitor.data.network.RetrofitClient

class AuthRepository {
    private val api = RetrofitClient.instance

    suspend fun login(email: String, password: String): Result<ApiResponse<LoginData>> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.success) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(email: String): Result<ApiResponse<Any?>> {
        return try {
            val response = api.forgotPassword(ForgotPasswordRequest(email))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
