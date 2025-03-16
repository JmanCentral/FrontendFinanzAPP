package com.practica.finazapp.Utils

import com.practica.finazapp.Entidades.CalificacionDTO
import com.practica.finazapp.Entidades.IngresoDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CalificacionService {

    // Registrar un ingreso
    @POST("RegistrarCalificacion")
    fun registrarCalificación(
        @Body calificacion: CalificacionDTO
    ): Call<CalificacionDTO>

    // Obtener ingresos casuales por año
    @GET("ObtenerCalificaciones")
    fun ObtenerCalificaciones(
    ): Call<List<CalificacionDTO>>
}