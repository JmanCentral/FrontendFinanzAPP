package com.practica.finazapp.Utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Cliente {
    fun getCliente(url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}