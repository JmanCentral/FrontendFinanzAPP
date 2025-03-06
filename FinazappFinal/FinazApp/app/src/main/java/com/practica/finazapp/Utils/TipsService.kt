package com.practica.finazapp.Utils

import com.practica.finazapp.Entidades.TipsDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TipsService {

    @GET("ObtenerTips/{usuarioId}")
    fun obtenerTips( @Path("usuarioId") idUsuario: Long): Call<List<TipsDTO>>
}