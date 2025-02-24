package com.practica.finazapp.RepositoriosApiRest

import android.content.Context
import com.practica.finazapp.Entidades.ConsejosDTO
import com.practica.finazapp.Utils.Cliente
import com.practica.finazapp.Utils.ConsejosService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConsejosRepository(context: Context)  {

    private val consejosService: ConsejosService by lazy {
        Cliente.getCliente("https://magicloops.dev/api/loop/1ddfa249-4cf1-44a9-ac22-3391b90b5d33/", context)
            .create(ConsejosService::class.java)
    }

    fun ObtenerConsejos(callback: (ConsejosDTO?, String?) -> Unit) {
        consejosService.obtenerConsejos().enqueue(object : Callback<ConsejosDTO> {
            override fun onResponse(call: Call<ConsejosDTO>, response: Response<ConsejosDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error en la solicitud: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ConsejosDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }
}
