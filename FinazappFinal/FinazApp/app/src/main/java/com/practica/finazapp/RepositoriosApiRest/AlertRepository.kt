package com.practica.finazapp.RepositoriosApiRest

import android.content.Context
import com.practica.finazapp.Entidades.AlertaDTO
import com.practica.finazapp.Utils.AlertaService
import com.practica.finazapp.Utils.Cliente
import com.practica.finazapp.Utils.UsuarioService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlertRepository (context: Context) {


    private val alertaService: AlertaService by lazy {
        Cliente.getCliente("http://192.168.10.15:8862/Finanzapp/Alerta/", context)
            .create(AlertaService::class.java)
    }

    fun registrarAlerta(idUsuario: Long, alerta: AlertaDTO, callback: (AlertaDTO?, String?) -> Unit) {
        alertaService.registrarAlerta(idUsuario, alerta).enqueue(object : Callback<AlertaDTO> {
            override fun onResponse(call: Call<AlertaDTO>, response: Response<AlertaDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al registrar alerta: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<AlertaDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }

        })

    }

    fun obtenerAlertaPorUser(idUsuario: Long, callback: (List<AlertaDTO>?, String?) -> Unit) {
        alertaService.obtenerAlertaPorUser(idUsuario).enqueue(object : Callback<List<AlertaDTO>> {
            override fun onResponse(call: Call<List<AlertaDTO>>, response: Response<List<AlertaDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
        } else {
            callback(null, "Error al obtener alertas por usuario: ${response.code()}")
        }
                }
            override fun onFailure(call: Call<List<AlertaDTO>>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
            }

    fun obtenerAlertaPorAnio(idUsuario: Long, callback: (List<AlertaDTO>?, String?) -> Unit) {
        alertaService.obtenerAlertaPorAnio(idUsuario).enqueue(object : Callback<List<AlertaDTO>> {
            override fun onResponse(call: Call<List<AlertaDTO>>, response: Response<List<AlertaDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
        } else {
            callback(null, "Error al obtener alertas por año: ${response.code()}")
        }
            }
            override fun onFailure(call: Call<List<AlertaDTO>>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun obtenerAlertaPorMes(idUsuario: Long, callback: (List<AlertaDTO>?, String?) -> Unit) {
        alertaService.obtenerAlertaPorMes(idUsuario).enqueue(object : Callback<List<AlertaDTO>> {
            override fun onResponse(call: Call<List<AlertaDTO>>, response: Response<List<AlertaDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)

        } else {
            callback(null, "Error al obtener alertas por mes: ${response.code()}")
        }

                }
            override fun onFailure(call: Call<List<AlertaDTO>>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })

            }

    fun modificarAlerta(idAlerta: Long, alerta: AlertaDTO, callback: (AlertaDTO?, String?) -> Unit) {
        alertaService.modificarAlerta(idAlerta, alerta).enqueue(object : Callback<AlertaDTO> {
            override fun onResponse(call: Call<AlertaDTO>, response: Response<AlertaDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)

        } else {
            callback(null, "Error al modificar alerta: ${response.code()}")
        }
                }
            override fun onFailure(call: Call<AlertaDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })

            }

    fun eliminarAlerta(idAlerta: Long, callback: (Boolean, String?) -> Unit) {
        alertaService.eliminarAlerta(idAlerta).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, "Error al eliminar Alerta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback(false, "Fallo en la conexión: ${t.message}")
            }
        })
    }

}