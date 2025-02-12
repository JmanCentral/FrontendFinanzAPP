package com.practica.finazapp.Utils

import com.practica.finazapp.Entidades.RecordatorioDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RecordatorioService {

    @POST("registro/Recordatorio/{id_usuario}")
    fun registrarRecordatorio(
        @Path("id_usuario") idUsuario: Long,
        @Body recordatorioDTO: RecordatorioDTO
    ): Call<RecordatorioDTO>

    @GET("ObtenerRecordatorios/{id_usuario}")
    fun listarRecordatorios(@Path("id_usuario") idUsuario: Long): Call<List<RecordatorioDTO>>

    @PUT("modificar/Recordatorio/{id_recordatorio}")
    fun modificarRecordatorio(
        @Path("id_recordatorio") idRecordatorio: Long,
        @Body recordatorioDTO: RecordatorioDTO
    ): Call<RecordatorioDTO>

    @DELETE("EliminarRecordatorios/{id_recordatorio}")
    fun eliminarRecordatorio(@Path("id_recordatorio") idRecordatorio: Long): Call<Void>

    @DELETE("eliminartodos/{id_usuario}")
    fun eliminarTodos(@Path("id_usuario") idUsuario: Long): Call <Void>

    @GET("BuscarPorNombre/{nombre}")
    fun buscarPorNombre(@Path("nombre") nombre: String): Call<List<RecordatorioDTO>>
}
