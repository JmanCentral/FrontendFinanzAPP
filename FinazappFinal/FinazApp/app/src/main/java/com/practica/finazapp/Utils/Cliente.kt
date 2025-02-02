package com.practica.finazapp.Utils

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Cliente {
    fun getCliente(url: String, context: Context): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context)) // Agregamos el interceptor aquí
            .build()

        return Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
