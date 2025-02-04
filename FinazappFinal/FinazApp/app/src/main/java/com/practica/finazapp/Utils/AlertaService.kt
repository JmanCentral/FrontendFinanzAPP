package com.practica.finazapp.Utils

import com.practica.finazapp.Entidades.AlertaDTO
import retrofit2.Call
import retrofit2.http.*

interface AlertaService {

    @POST("RegistrarAlerta/{id_usuario}")
    fun registrarAlerta(
        @Path("id_usuario") idUsuario: Long,
        @Body alerta: AlertaDTO
    ): Call<AlertaDTO>

    @GET("ObtenerAlertaPorUser/{id_usuario}")
    fun obtenerAlertaPorUser(
        @Path("id_usuario") idUsuario: Long
    ): Call<List<AlertaDTO>>

    @GET("ObtenerAlertaPorAnio/{id_usuario}")
    fun obtenerAlertaPorAnio(
        @Path("id_usuario") idUsuario: Long
    ): Call<List<AlertaDTO>>

    @GET("ObtenerAlertaPorMes/{id_usuario}")
    fun obtenerAlertaPorMes(
        @Path("id_usuario") idUsuario: Long
    ): Call<List<AlertaDTO>>

    @PUT("ModificarAlerta/{id_alerta}")
    fun modificarAlerta(
        @Path("id_alerta") idAlerta: Long,
        @Body alerta: AlertaDTO
    ): Call<AlertaDTO>

    @DELETE("EliminarAlertas/{id_alerta}")
    fun eliminarAlerta(
        @Path("id_alerta") idAlerta: Long
    ): Call<Void>
}