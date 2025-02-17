package com.practica.finazapp.Utils

import com.practica.finazapp.Entidades.EmailRequest

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RecuperadorService {

    @POST("forgot")
    fun recuperarpassword(@Body recuperador: EmailRequest): Call<EmailRequest>
}