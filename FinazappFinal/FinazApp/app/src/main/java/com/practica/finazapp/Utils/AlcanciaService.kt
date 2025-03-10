package com.practica.finazapp.Utils

import com.practica.finazapp.Entidades.AlcanciaDTO
import com.practica.finazapp.Entidades.UsuarioDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AlcanciaService {
    @POST("RegistrarAlcancia/{id_usuario}")
    fun crearAlcancia(@Path("id_usuario") idUsuario: Long, @Body alcancia: AlcanciaDTO): Call<AlcanciaDTO>

    @GET("BuscarPorCodigo/{codigo}")
    fun obtenerAlcancia(@Path("codigo") codigo: String): Call<List<AlcanciaDTO>>

    @GET("BuscarAlcancias/{id_usuario}")
    fun obtenerAlcanciasporuser(@Path("id_usuario") idUsuario: Long): Call<List<AlcanciaDTO>>

    @GET("BuscarAlcanciasPorUsuario/{id_alcancia}")
    fun obtenerAlcanciasNotificacion(@Path("id_alcancia") idAlcancia: Long): Call<List<UsuarioDTO>>

    @PUT("ModificarAlcancias/{id_alcancia}")
    fun actualizarAlcancia(@Path("id_alcancia") idAlcancia: Long, @Body alcancia: AlcanciaDTO): Call<AlcanciaDTO>

    @DELETE("EliminarAlcancia/{id_alcancia}")
    fun eliminarAlcancia(@Path("id_alcancia") idAlcancia: Long): Call<Void>



}