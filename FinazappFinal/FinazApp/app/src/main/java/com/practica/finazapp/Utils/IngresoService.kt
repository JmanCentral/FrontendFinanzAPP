package com.practica.finazapp.Utils

import com.practica.finazapp.Entidades.IngresoDTO
import retrofit2.Call
import retrofit2.http.*

interface IngresoService {

    // Registrar un ingreso
    @POST("registrarIngreso/{id_usuario}")
    fun registrarIngreso(
        @Path("id_usuario") idUsuario: Long,
        @Body ingreso: IngresoDTO
    ): Call<IngresoDTO>

    // Obtener ingresos casuales por año
    @GET("IngresosCasualesAnio/{id_usuario}")
    fun listarIngresosCasualesPorAnio(
        @Path("id_usuario") idUsuario: Long
    ): Call<List<IngresoDTO>>

    // Obtener ingresos mensuales
    @GET("IngresosMensuales/{id_usuario}")
    fun listarIngresosMensuales(
        @Path("id_usuario") idUsuario: Long
    ): Call<List<IngresoDTO>>

    // Obtener ingresos casuales
    @GET("IngresosCasuales/{id_usuario}")
    fun listarIngresosCasuales(
        @Path("id_usuario") idUsuario: Long
    ): Call<List<IngresoDTO>>

    // Obtener el total de ingresos
    @GET("ingresostotal/{id_usuario}")
    fun obtenerTotalIngresos(
        @Path("id_usuario") idUsuario: Long
    ): Call<Double>

    // Obtener ingresos mensuales por año y mes
    @GET("ingresosmensuales/{id_usuario}/{anio}/{mes}")
    fun getIngresosMensuales(
        @Path("id_usuario") idUsuario: Long,
        @Path("anio") anio: Int,
        @Path("mes") mes: Int
    ): Call<List<IngresoDTO>>

    // Obtener proyección de ingresos
    @GET("ProyeccionesIngreso/{id_usuario}")
    fun obtenerProyeccionIngresos(
        @Path("id_usuario") idUsuario: Long
    ): Call<Double>

    // Obtener proyección de ingresos
    @GET("AhorroMensual/{id_usuario}")
    fun obtenerAhorroPotencial(
        @Path("id_usuario") idUsuario: Long
    ): Call<Double>

    // Modificar un ingreso
    @PUT("modificar/{id_ingreso}")
    fun modificarIngreso(
        @Path("id_ingreso") idIngreso: Long,
        @Body ingresoDTO: IngresoDTO
    ): Call<IngresoDTO>

    // Eliminar un ingreso
    @DELETE("EliminarIngresos/{id_ingreso}")
    fun eliminarIngreso(
        @Path("id_ingreso") idIngreso: Long
    ): Call<Void>
}