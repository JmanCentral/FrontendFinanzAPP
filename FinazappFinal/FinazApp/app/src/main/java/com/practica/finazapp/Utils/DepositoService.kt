package com.practica.finazapp.Utils

import com.practica.finazapp.Entidades.DepositoDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DepositoService {

    @POST("RegistrarDeposito/{id_usuario}/{id_alcancia}")
    fun registrarDeposito(
        @Body depositoDTO: DepositoDTO,
        @Path("id_usuario") idUsuario: Long,
        @Path("id_alcancia") idAlcancia: Long
    ): Call<DepositoDTO>

    @GET("ObtenerDepositos/{id_alcancia}")
    fun obtenerDepositos(@Path("id_alcancia") idAlcancia: Long): Call<List<DepositoDTO>>

    @GET("ObtenerValorGastosMesDeposito/{id_usuario}")
    fun obtenerValorGastosMesDeposito(
        @Path("id_usuario") idUsuario: Long
    ): Call<Double>

    @PUT("ModificarDepositos/{id_deposito}/{id_alcancia}")
    fun modificarDepositos(
        @Body depositoDTO: DepositoDTO,
        @Path("id_deposito") idDeposito: Long,
        @Path("id_alcancia") idAlcancia: Long
    ): Call<DepositoDTO>

    @DELETE("EliminarDeposito/{id_deposito}/{id_alcancia}")
    fun eliminarDepositos(
        @Path("id_deposito") idDeposito: Long,
        @Path("id_alcancia") idAlcancia: Long
    ): Call<Void>


}