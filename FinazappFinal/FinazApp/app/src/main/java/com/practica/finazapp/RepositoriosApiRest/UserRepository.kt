package com.practica.finazapp.Repositories

import com.practica.finazapp.Entidades.UsuarioDTO
import com.practica.finazapp.Utils.Cliente
import com.practica.finazapp.Utils.UsuarioService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {

    private val usuarioService: UsuarioService =
        Cliente.getCliente("http://192.168.10.6:8862/Finanzapp/").create(UsuarioService::class.java)

    fun registrarUsuario(usuario: UsuarioDTO, callback: (UsuarioDTO?, String?) -> Unit) {
        usuarioService.registrarUsuario(usuario).enqueue(object : Callback<UsuarioDTO> {
            override fun onResponse(call: Call<UsuarioDTO>, response: Response<UsuarioDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al registrar usuario: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UsuarioDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun loginUsuario(usuario: UsuarioDTO, callback: (UsuarioDTO?, String?) -> Unit) {
        usuarioService.loginUsuario(usuario).enqueue(object : Callback<UsuarioDTO> {
            override fun onResponse(call: Call<UsuarioDTO>, response: Response<UsuarioDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al iniciar sesión: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UsuarioDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
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
