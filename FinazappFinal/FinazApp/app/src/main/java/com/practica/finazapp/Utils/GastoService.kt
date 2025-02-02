package com.practica.finazapp.Utils

import com.practica.finazapp.Entidades.GastoDTO
import retrofit2.Call
import retrofit2.http.*

interface GastoService {

    // Registrar un gasto
    @POST("RegistrarGasto/{id_usuario}")
     fun registrarGasto(
        @Path("id_usuario") idUsuario: Long,
        @Body gasto: GastoDTO
    ): Call<GastoDTO>

    // Obtener dinero disponible
    @GET("ObtenerDineroDisponible/{id_usuario}")
     fun obtenerDineroDisponible(
        @Path("id_usuario") idUsuario: Long
    ): Call<Double>

    // Obtener gastos por mes y categoría
    @GET("GastosMesCategoria/{id_usuario}/{categoria}")
     fun obtenerGastosMesCategoria(
        @Path("id_usuario") idUsuario: Long,
        @Path("categoria") categoria: String
    ): Call<List<GastoDTO>>

    // Obtener valor general de gastos por mes y categoría
    @GET("ObtenerValorGastosMesCategoria/{id_usuario}/{categoria}")
     fun obtenerValorGastosMesCategoria(
        @Path("id_usuario") idUsuario: Long,
        @Path("categoria") categoria: String
    ): Call<Double>

    // Obtener valor general de gastos por mes
    @GET("ObtenerValorGastosMes/{id_usuario}")
     fun obtenerValorGastosMes(
        @Path("id_usuario") idUsuario: Long
    ): Call<Double>

    // Obtener gastos por fechas
    @GET("GastosMesCategoria/{id_usuario}/{fecha_inicial}/{fecha_final}")
     fun listarGastosPorFechas(
        @Path("id_usuario") idUsuario: Long,
        @Path("fecha_inicial") fechaInicial: String,
        @Path("fecha_final") fechaFinal: String
    ): Call<List<GastoDTO>>

    // Obtener gastos ordenados ascendentemente
    @GET("ObtenerGastosAscendentemente/{id_usuario}")
     fun listarGastosAscendentemente(
        @Path("id_usuario") idUsuario: Long
    ): Call<List<GastoDTO>>

    // Obtener gastos ordenados por valor alto
    @GET("ObtenerGastoAlto/{id_usuario}")
     fun listarGastosAlto(
        @Path("id_usuario") idUsuario: Long
    ): Call<List<GastoDTO>>

    // Obtener gastos ordenados por valor bajo
    @GET("ObtenerGastoBajo/{id_usuario}")
     fun listarGastosBajo(
        @Path("id_usuario") idUsuario: Long
    ): Call<List<GastoDTO>>

    // Obtener gastos ordenados descendentemente
    @GET("ObtenerGastosDescendentemente/{id_usuario}")
     fun listarGastosDescendentemente(
        @Path("id_usuario") idUsuario: Long
    ): Call<List<GastoDTO>>

    // Obtener promedio de gastos
    @GET("ObtenerPromedioGastos/{id_usuario}")
     fun obtenerPromedioGastos(
        @Path("id_usuario") idUsuario: Long
    ): Call<Double>

    // Obtener gasto recurrente
    @GET("ObtenerGastoRecurrente/{id_usuario}")
     fun obtenerGastoRecurrente(
        @Path("id_usuario") idUsuario: Long
    ): Call<String>

    // Obtener porcentaje de gastos sobre ingresos
    @GET("ObtenerPorcentaje/{id_usuario}")
     fun obtenerPorcentajeGastos(
        @Path("id_usuario") idUsuario: Long
    ): Call<Double>

    // Obtener promedio diario de gastos
    @GET("ObtenerPromedioDiario/{id_usuario}")
     fun obtenerPromedioDiario(
        @Path("id_usuario") idUsuario: Long
    ): Call<Double>

    // Modificar un gasto
    @PUT("ModificarGastos/{id_gasto}")
     fun modificarGasto(
        @Path("id_gasto") idGasto: Long,
        @Body gasto: GastoDTO
    ): Call<GastoDTO>

    // Eliminar un gasto
    @DELETE("EliminarGastos/{id_gasto}")
     fun eliminarGasto(
        @Path("id_gasto") idGasto: Long
    ): Call<Void>
}