package com.practica.finazapp.RepositoriosApiRest

import android.content.Context
import android.util.Log
import com.practica.finazapp.Entidades.IngresoDTO
import com.practica.finazapp.Utils.Cliente
import com.practica.finazapp.Utils.IngresoService
import com.practica.finazapp.Utils.UsuarioService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IncomeRepository(context: Context) {

    private val ingresoService: IngresoService by lazy {
        Cliente.getCliente("http://192.168.10.6:8862/Finanzapp/Ingresos/", context)
            .create(IngresoService::class.java)
    }

    fun registrarIngreso(idUsuario: Long, ingreso: IngresoDTO, callback: (IngresoDTO?, String?) -> Unit) {
        ingresoService.registrarIngreso(idUsuario, ingreso).enqueue(object : Callback<IngresoDTO> {
            override fun onResponse(call: Call<IngresoDTO>, response: Response<IngresoDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al registrar ingreso: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<IngresoDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun listarIngresosCasualesPorAnio(idUsuario: Long, callback: (List<IngresoDTO>?, String?) -> Unit) {
        Log.d("IngresoRepository", "Llamando a listarIngresosCasualesPorAnio con idUsuario: $idUsuario")

        ingresoService.listarIngresosCasualesPorAnio(idUsuario).enqueue(object : Callback<List<IngresoDTO>> {
            override fun onResponse(call: Call<List<IngresoDTO>>, response: Response<List<IngresoDTO>>) {
                Log.d("IngresoRepository", "Código de respuesta: ${response.code()}")

                if (response.isSuccessful) {
                    val ingresos = response.body()
                    Log.d("IngresoRepository", "Ingresos casuales recibidos: $ingresos")

                    if (ingresos.isNullOrEmpty()) {
                        Log.e("IngresoRepository", "La API respondió, pero la lista de ingresos está vacía")
                    }

                    callback(ingresos, null)
                } else {
                    val errorMsg = "Error al obtener ingresos casuales: Código ${response.code()}, mensaje: ${response.message()}"
                    Log.e("IngresoRepository", errorMsg)
                    Log.e("IngresoRepository", "Cuerpo del error: ${response.errorBody()?.string()}")
                    callback(null, errorMsg)
                }
            }

            override fun onFailure(call: Call<List<IngresoDTO>>, t: Throwable) {
                val errorMsg = "Fallo en la conexión: ${t.message}"
                Log.e("IngresoRepository", errorMsg, t)
                callback(null, errorMsg)
            }
        })
    }

    fun obtenerAhorroPotencial (idUsuario: Long, callback: (Double?, String?) -> Unit) {
        ingresoService.obtenerAhorroPotencial(idUsuario).enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener el ahorro potencial: ${response.code()}")
                }

                }
            override fun onFailure(call: Call<Double>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }



    fun listarIngresosMensuales(idUsuario: Long, callback: (List<IngresoDTO>?, String?) -> Unit) {
        ingresoService.listarIngresosMensuales(idUsuario).enqueue(object : Callback<List<IngresoDTO>> {
            override fun onResponse(
                call: Call<List<IngresoDTO>>,
                response: Response<List<IngresoDTO>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener ingresos mensuales: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<IngresoDTO>>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })

    }

    fun listarIngresosCasuales(idUsuario: Long, callback: (List<IngresoDTO>?, String?) -> Unit) {
        ingresoService.listarIngresosCasuales(idUsuario).enqueue(object : Callback<List<IngresoDTO>> {
            override fun onResponse(
                call: Call<List<IngresoDTO>>,
                response: Response<List<IngresoDTO>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener ingresos casuales: ${response.code()}")
                }

            }
            override fun onFailure(call: Call<List<IngresoDTO>>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })

    }

    fun obtenerTotalIngresos(idUsuario: Long, callback: (Double?, String?) -> Unit) {
        ingresoService.obtenerTotalIngresos(idUsuario).enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener el total de ingresos: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<Double>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun getIngresosMensuales(idUsuario: Long, anio: Int, mes: Int, callback: (List<IngresoDTO>?, String?) -> Unit) {
        ingresoService.getIngresosMensuales(idUsuario, anio, mes).enqueue(object : Callback<List<IngresoDTO>> {
            override fun onResponse(call: Call<List<IngresoDTO>>, response: Response<List<IngresoDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener ingresos mensuales: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<IngresoDTO>>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
                }
        })

    }

    fun obtenerProyeccionIngresos(idUsuario: Long, callback: (Double?, String?) -> Unit) {
        ingresoService.obtenerProyeccionIngresos(idUsuario).enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                if (response.isSuccessful) {
                    callback(response.body(), null) // Devuelve el valor si la respuesta es exitosa
                } else {
                    callback(null, "Error al obtener la proyección de ingresos: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun modificarIngreso(idIngreso: Long, ingresoDTO: IngresoDTO, callback: (IngresoDTO?, String?) -> Unit) {
        ingresoService.modificarIngreso(idIngreso, ingresoDTO).enqueue(object : Callback<IngresoDTO> {
            override fun onResponse(call: Call<IngresoDTO>, response: Response<IngresoDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al modificar ingreso: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<IngresoDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })

        }

    fun eliminarIngreso(idIngreso: Long, callback: (Boolean, String?) -> Unit) {
        ingresoService.eliminarIngreso(idIngreso).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, "Error al eliminar ingreso: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback(false, "Fallo en la conexión: ${t.message}")
            }
        })
    }

}