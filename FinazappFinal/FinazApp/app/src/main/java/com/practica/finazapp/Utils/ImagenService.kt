package com.practica.finazapp.Utils

import com.practica.finazapp.Entidades.ImagenDTO
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ImagenService {
    @Multipart
    @POST("imagenes/guardar")
    fun guardarImagen(
        @Part("idAlcancia") idAlcancia: RequestBody?,
        @Part archivo: Part?
    ): Call<ImagenDTO?>?
}