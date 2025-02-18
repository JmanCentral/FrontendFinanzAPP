package com.example.finanzapp.repositories

import android.content.Context
import com.practica.finazapp.Entidades.AlcanciaDTO
import com.practica.finazapp.Utils.AlcanciaService
import com.practica.finazapp.Utils.Cliente
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AlcanciaRepository(context: Context) {

    private val alcanciaService: AlcanciaService by lazy {
        Cliente.getCliente("http://192.168.10.3:8862/Finanzapp/Alcancias/", context)
            .create(AlcanciaService::class.java)
    }


        fun registrarAlcancia(
            idUsuario: Long,
            nombre: String,
            meta: Double,
            saldoActual: Double,
            codigo: String,
            fecha: String,
            imagen: File,
            callback: (AlcanciaDTO?, String?) -> Unit
        ) {
            // Crear RequestBody para cada campo
            val nombreBody = RequestBody.create("text/plain".toMediaType(), nombre)
            val metaBody = RequestBody.create("text/plain".toMediaType(), meta.toString())
            val saldoBody = RequestBody.create("text/plain".toMediaType(), saldoActual.toString())
            val codigoBody = RequestBody.create("text/plain".toMediaType(), codigo)
            val fechaBody = RequestBody.create("text/plain".toMediaType(), fecha)

            // Crear MultipartBody.Part para la imagen
            val requestFile = RequestBody.create("image/*".toMediaType(), imagen)
            val imagenPart = MultipartBody.Part.createFormData("imagen", imagen.name, requestFile)

            // Llamada al servicio
            alcanciaService.registrarAlcancia(
                idUsuario, nombreBody, metaBody, saldoBody, codigoBody, fechaBody, imagenPart
            ).enqueue(object : Callback<AlcanciaDTO> {
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
