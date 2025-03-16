package com.practica.finazapp.RepositoriosApiRest

import android.content.Context
import com.practica.finazapp.Entidades.EmailRequest
import com.practica.finazapp.Entidades.UsuarioDTO
import com.practica.finazapp.Utils.Cliente
import com.practica.finazapp.Utils.RecuperadorService
import com.practica.finazapp.Utils.UsuarioService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordRepository (context: Context) {

    private val recuperadorService: RecuperadorService by lazy {
<<<<<<< HEAD
        Cliente.getCliente("http://100.106.18.126:8862/api/password/", context)
=======
        Cliente.getCliente("http://100.115.249.2:8862/api/password/", context)
>>>>>>> develop
            .create(RecuperadorService::class.java)
    }


    fun recuperarContrasena(recuperador: EmailRequest, callback: (EmailRequest?, String?) -> Unit) {
        recuperadorService.recuperarpassword(recuperador).enqueue(object : Callback<EmailRequest> {
            override fun onResponse(call: Call<EmailRequest>, response: Response<EmailRequest>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    onFailure(call, Throwable("Error en la solicitud: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<EmailRequest>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }
}