package com.example.finanzapp.repositories

import android.content.Context
import com.practica.finazapp.Entidades.AlcanciaDTO
import com.practica.finazapp.Utils.AlcanciaService
import com.practica.finazapp.Utils.Cliente
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlcanciaRepository(context: Context) {

    private val alcanciaService: AlcanciaService by lazy {
        Cliente.getCliente("http://100.108.80.90:8862/Finanzapp/Alcancias/", context)
            .create(AlcanciaService::class.java)
    }

    // Registrar una nueva alcancía
    fun registrarAlcancia(alcancia: AlcanciaDTO, idUsuario: Long, callback: (AlcanciaDTO?, String?) -> Unit) {
        alcanciaService.crearAlcancia(idUsuario, alcancia).enqueue(object : Callback<AlcanciaDTO> {
            override fun onResponse(call: Call<AlcanciaDTO>, response: Response<AlcanciaDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al registrar alcancía: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AlcanciaDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    // Buscar alcancía por código
    fun buscarPorCodigo(codigo: String, callback: (List<AlcanciaDTO>?, String?) -> Unit) {
        alcanciaService.obtenerAlcancia(codigo).enqueue(object : Callback<List<AlcanciaDTO>> {
            override fun onResponse(call: Call<List<AlcanciaDTO>>, response: Response<List<AlcanciaDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener alcancía: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<AlcanciaDTO>>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun obtenerAlcanciasporuser(idUsuario: Long, callback: (List<AlcanciaDTO>?, String?) -> Unit) {
        alcanciaService.obtenerAlcanciasporuser(idUsuario).enqueue(object : Callback<List<AlcanciaDTO>> {
            override fun onResponse(call: Call<List<AlcanciaDTO>>, response: Response<List<AlcanciaDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener alcancias: ${response.code()}")
                }
                }
            override fun onFailure(call: Call<List<AlcanciaDTO>>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }
}
