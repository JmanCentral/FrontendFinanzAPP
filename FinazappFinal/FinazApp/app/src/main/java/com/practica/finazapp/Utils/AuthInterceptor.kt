package com.practica.finazapp.Utils

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {
    private val sharedPreferences = context.getSharedPreferences("MiApp", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sharedPreferences.getString("TOKEN", null)
        val request = chain.request().newBuilder()

        if (!token.isNullOrEmpty()) {
            request.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(request.build())
    }
}
