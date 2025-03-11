package com.practica.finazapp.RepositoriosApiRest

import android.content.Context
import com.practica.finazapp.Entidades.CalificacionDTO
import com.practica.finazapp.Entidades.DepositoDTO
import com.practica.finazapp.Utils.CalificacionService
import com.practica.finazapp.Utils.Cliente
import com.practica.finazapp.Utils.DepositoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CalificacionRepository (context: Context) {

    private val calificacionService: CalificacionService by lazy {
        Cliente.getCliente("http://100.115.249.2:8862/Finanzapp/calificaciones/", context)
            .create(CalificacionService::class.java)
    }

    fun registrarCalificacion(calificacionDTO: CalificacionDTO, callback: (CalificacionDTO?, String?) -> Unit) {
        calificacionService.registrarCalificación(calificacionDTO).enqueue(object : Callback<CalificacionDTO> {
            override fun onResponse(
                call: Call<CalificacionDTO>,
                response: Response<CalificacionDTO>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error en la solicitud: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CalificacionDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
            })
        }


    fun obtenerCalificaciones(callback: (List<CalificacionDTO>?, String?) -> Unit) {
        calificacionService.ObtenerCalificaciones().enqueue(object : Callback<List<CalificacionDTO>> {
            override fun onResponse(call: Call<List<CalificacionDTO>>, response: Response<List<CalificacionDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener calificaciones: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<CalificacionDTO>>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")

                }
        })

    }
}
