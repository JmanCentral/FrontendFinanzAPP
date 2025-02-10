package com.practica.finazapp.Repositories

import android.content.Context
import com.practica.finazapp.Entidades.LoginDTO
import com.practica.finazapp.Entidades.LoginResponseDTO
import com.practica.finazapp.Entidades.UsuarioDTO
import com.practica.finazapp.Utils.Cliente
import com.practica.finazapp.Utils.UsuarioService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository (context: Context) {

    private val usuarioService: UsuarioService by lazy {
        Cliente.getCliente("http://192.168.10.6:8862/Finanzapp/", context)
            .create(UsuarioService::class.java)
    }


    fun registrarUsuario(usuario: UsuarioDTO, callback: (UsuarioDTO?, String?) -> Unit) {
        usuarioService.registrarUsuario(usuario).enqueue(object : Callback<UsuarioDTO> {
            override fun onResponse(call: Call<UsuarioDTO>, response: Response<UsuarioDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Contraseña incorrecta")
                }
            }

            override fun onFailure(call: Call<UsuarioDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    // ✅ Login de usuario
    fun iniciarSesion(username: String, contrasena: String, callback: (LoginResponseDTO?, String?) -> Unit) {
        val loginRequest = LoginDTO(username, contrasena)
        usuarioService.loginUsuario(loginRequest).enqueue(object : Callback<LoginResponseDTO> {
            override fun onResponse(call: Call<LoginResponseDTO>, response: Response<LoginResponseDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error en el login: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponseDTO>, t: Throwable) {
                callback(null, "Error de conexión: ${t.message}")
            }
        })
    }

    fun ObtenerUsuario(idUsuario: Long, callback: (UsuarioDTO?, String?) -> Unit) {
        usuarioService.ObtenerUsuario(idUsuario).enqueue(object : Callback<UsuarioDTO> {
            override fun onResponse(call: Call<UsuarioDTO>, response: Response<UsuarioDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener usuario: ${response.code()}")
                }
                }
            override fun onFailure(call: Call<UsuarioDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }




}
