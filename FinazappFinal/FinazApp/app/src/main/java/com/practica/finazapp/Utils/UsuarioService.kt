package com.practica.finazapp.Utils

import com.practica.finazapp.Entidades.LoginDTO
import com.practica.finazapp.Entidades.LoginResponseDTO
import com.practica.finazapp.Entidades.UsuarioDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UsuarioService {

    @POST("registro")
    fun registrarUsuario(@Body usuario: UsuarioDTO): Call<UsuarioDTO>

    @POST("login")
    fun loginUsuario(@Body loginRequest: LoginDTO): Call<LoginResponseDTO>

    @GET("ObtenerUsuario/{id_usuario}")
    fun ObtenerUsuario(@Path("id_usuario") idUsuario: Long): Call<UsuarioDTO>
}
