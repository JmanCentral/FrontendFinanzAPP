package com.practica.finazapp.Utils

import com.practica.finazapp.Entidades.AlcanciaDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface AlcanciaService {

    @Multipart
    @POST("RegistrarAlcancia/{id_usuario}")
    fun registrarAlcancia(
        @Path("id_usuario") idUsuario: Long,
        @Part("nombre_alcancia") nombreAlcancia: RequestBody,
        @Part("meta") meta: RequestBody,
        @Part("saldoActual") saldoActual: RequestBody,
        @Part("codigo") codigo: RequestBody,
        @Part("fechaCreacion") fechaCreacion: RequestBody,
        @Part imagen: MultipartBody.Part
    ): Call<AlcanciaDTO>

    @GET("BuscarPorCodigo/{codigo}")
    fun obtenerAlcancia(@Path("codigo") codigo: String): Call<List<AlcanciaDTO>>

    @GET("BuscarAlcancias/{id_usuario}")
    fun obtenerAlcanciasporuser(@Path("id_usuario") idUsuario: Long): Call<List<AlcanciaDTO>>
}