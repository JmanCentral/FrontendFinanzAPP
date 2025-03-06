package com.practica.finazapp.RepositoriosApiRest

import android.content.Context
import android.util.Log
import com.practica.finazapp.Entidades.ConsejosDTO
import com.practica.finazapp.Utils.Cliente
import com.practica.finazapp.Utils.ConsejosService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConsejosRepository(context: Context)  {

    private val consejosService: ConsejosService by lazy {
        Cliente.getCliente("http://192.168.10.15:8862/Finanzapp/Consejos/", context)
            .create(ConsejosService::class.java)
    }

    fun ObtenerConsejos(callback: (List<ConsejosDTO>?, String?) -> Unit) {
        Log.d("ObtenerConsejos", "Iniciando la petición a la API...")
        consejosService.obtenerConsejos().enqueue(object : Callback<List<ConsejosDTO>> {
            override fun onResponse(call: Call<List<ConsejosDTO>>, response: Response<List<ConsejosDTO>>) {
                if (response.isSuccessful) {
                    Log.d("ObtenerConsejos", "Respuesta exitosa: ${response.body()?.size} consejos recibidos")
                    callback(response.body(), null)
                } else {
                    Log.e("ObtenerConsejos", "Error en la solicitud: ${response.code()} - ${response.message()}")
                    callback(null, "Error en la solicitud: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ConsejosDTO>>, t: Throwable) {
                Log.e("ObtenerConsejos", "Fallo en la conexión: ${t.message}")
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }
}
