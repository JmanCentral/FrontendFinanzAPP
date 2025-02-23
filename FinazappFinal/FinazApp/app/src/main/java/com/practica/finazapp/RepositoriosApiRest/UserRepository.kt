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
        Cliente.getCliente("http://100.115.249.2:8862/Finanzapp/", context)
            .create(UsuarioService::class.java)
    }


    fun registrarUsuario(usuario: UsuarioDTO, callback: (UsuarioDTO?, String?) -> Unit) {
        usuarioService.registrarUsuario(usuario).enqueue(object : Callback<UsuarioDTO> {
            override fun onResponse(call: Call<UsuarioDTO>, response: Response<UsuarioDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    // 🔴 Manejar error 400 como si fuera un fallo
                    onFailure(call, Throwable("Error en la solicitud: ${response.code()}"))
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
                    when (response.code()) {
                        401, 403 -> callback(
                            null,
                            "Acceso denegado: Usuario o contraseña incorrectos"
                        )  // ✅ Mensaje más preciso
                        else -> callback(
                            null,
                            "Error inesperado: ${response.code()}"
                        )  // ❗ Maneja otros errores
                    }
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
