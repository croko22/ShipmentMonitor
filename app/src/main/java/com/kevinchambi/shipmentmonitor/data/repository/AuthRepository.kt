package com.kevinchambi.shipmentmonitor.data.repository

import com.kevinchambi.shipmentmonitor.data.model.*
import com.kevinchambi.shipmentmonitor.data.network.RetrofitClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException

class AuthRepository {
    private val api = RetrofitClient.instance
    private val gson = Gson()

    suspend fun login(email: String, password: String): Result<LoginData> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error desconocido"))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody) ?: "No está autorizado a ingresar a la aplicación"
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión. Verifica tu acceso a internet."))
        }
    }

    suspend fun forgotPassword(email: String): Result<String> {
        return try {
            val response = api.forgotPassword(ForgotPasswordRequest(email))
            Result.success(response.message ?: "La solicitud de restablecimiento de contraseña fue enviada con éxito.")
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody) ?: "Error al processar la solicitud"
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión. Verifica tu acceso a internet."))
        }
    }

    private fun parseErrorMessage(errorBody: String?): String? {
        return try {
            val type = object : TypeToken<ApiResponse<Nothing>>() {}.type
            val errorResponse = gson.fromJson<ApiResponse<Nothing>>(errorBody, type)
            errorResponse?.message
        } catch (e: Exception) {
            null
        }
    }
}
