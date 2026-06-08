package com.kevinchambi.shipmentmonitor.data.network

import com.kevinchambi.shipmentmonitor.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = sessionManager.getToken()

        val request = if (token != null && shouldAddToken(originalRequest.url.toString())) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }

    private fun shouldAddToken(url: String): Boolean {
        return !url.contains("auth/login") && !url.contains("auth/forgot")
    }
}
