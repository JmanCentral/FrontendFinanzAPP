package com.practica.finazapp.RepositoriosApiRest

import android.content.Context
import com.practica.finazapp.Entidades.RecordatorioDTO
import com.practica.finazapp.Utils.Cliente
import com.practica.finazapp.Utils.RecordatorioService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReminderRepository (context: Context) {

    private val recordatorioService: RecordatorioService by lazy {
        Cliente.getCliente("https://backendfinazapp-1.onrender.com/Finanzapp/Recordatorios/", context)
            .create(RecordatorioService::class.java)
    }

    // ✅ Registrar recordatorio
    fun registrarRecordatorio(idUsuario: Long, recordatorioDTO: RecordatorioDTO, callback: (RecordatorioDTO?, String?) -> Unit) {
        recordatorioService.registrarRecordatorio(idUsuario, recordatorioDTO)
            .enqueue(object : Callback<RecordatorioDTO> {
                override fun onResponse(call: Call<RecordatorioDTO>, response: Response<RecordatorioDTO>) {
                    if (response.isSuccessful) {
                        callback(response.body(), null)
                    } else {
                        callback(null, "Error al registrar recordatorio: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<RecordatorioDTO>, t: Throwable) {
                    callback(null, "Fallo en la conexión: ${t.message}")
                }
            })
    }

    // ✅ Listar recordatorios por usuario
    fun listarRecordatorios(idUsuario: Long, callback: (List<RecordatorioDTO>?, String?) -> Unit) {
        recordatorioService.listarRecordatorios(idUsuario)
            .enqueue(object : Callback<List<RecordatorioDTO>> {
                override fun onResponse(call: Call<List<RecordatorioDTO>>, response: Response<List<RecordatorioDTO>>) {
                    if (response.isSuccessful) {
                        callback(response.body(), null)
                    } else {
                        callback(null, "Error al obtener recordatorios: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<RecordatorioDTO>>, t: Throwable) {
                    callback(null, "Fallo en la conexión: ${t.message}")
                }
            })
    }

    // ✅ Modificar un recordatorio
    fun modificarRecordatorio(idRecordatorio: Long, recordatorioDTO: RecordatorioDTO, callback: (RecordatorioDTO?, String?) -> Unit) {
        recordatorioService.modificarRecordatorio(idRecordatorio, recordatorioDTO)
            .enqueue(object : Callback<RecordatorioDTO> {
                override fun onResponse(call: Call<RecordatorioDTO>, response: Response<RecordatorioDTO>) {
                    if (response.isSuccessful) {
                        callback(response.body(), null)
                    } else {
                        callback(null, "Error al modificar recordatorio: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<RecordatorioDTO>, t: Throwable) {
                    callback(null, "Fallo en la conexión: ${t.message}")
                }
            })
    }

    // ✅ Eliminar un recordatorio
    fun eliminarRecordatorio(idRecordatorio: Long, callback: (Boolean, String?) -> Unit) {
        recordatorioService.eliminarRecordatorio(idRecordatorio)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        callback(true, null)
                    } else {
                        callback(false, "Error al eliminar recordatorio: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    callback(false, "Fallo en la conexión: ${t.message}")
                }
            })
    }

    // ✅ Eliminar todos los recordatorios
    fun eliminarTodos(callback: (String?, String?) -> Unit) {
        recordatorioService.eliminarTodos()
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        callback(response.body(), null)
                    } else {
                        callback(null, "Error al eliminar todos los recordatorios: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    callback(null, "Fallo en la conexión: ${t.message}")
                }
            })
    }

    // ✅ Buscar un recordatorio por nombre
    fun buscarPorNombre(nombre: String, callback: (RecordatorioDTO?, String?) -> Unit) {
        recordatorioService.buscarPorNombre(nombre)
            .enqueue(object : Callback<RecordatorioDTO> {
                override fun onResponse(call: Call<RecordatorioDTO>, response: Response<RecordatorioDTO>) {
                    if (response.isSuccessful) {
                        callback(response.body(), null)
                    } else {
                        callback(null, "Error al buscar recordatorio: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<RecordatorioDTO>, t: Throwable) {
                    callback(null, "Fallo en la conexión: ${t.message}")
                }
            })
    }

}