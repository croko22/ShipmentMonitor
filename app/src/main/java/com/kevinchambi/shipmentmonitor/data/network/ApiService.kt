package com.kevinchambi.shipmentmonitor.data.network

import com.kevinchambi.shipmentmonitor.data.model.*
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginData>

    @POST("auth/forgot")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ApiResponse<Any?>

    @GET("vehicles")
    suspend fun getVehicles(): ApiResponse<List<Vehicle>>

    @GET("notifications")
    suspend fun getNotifications(): ApiResponse<List<Notification>>
}
