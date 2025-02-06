package com.practica.finazapp.RepositoriosApiRest

import android.content.Context
import com.practica.finazapp.Entidades.GastoDTO
import com.practica.finazapp.Utils.Cliente
import com.practica.finazapp.Utils.GastoService
import com.practica.finazapp.Utils.UsuarioService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SpendRepository (context: Context)  {

    private val gastoService: GastoService by lazy {
        Cliente.getCliente("http://192.168.10.6:8862/Finanzapp/Gastos/", context)
            .create(GastoService::class.java)
    }

    fun registrarGasto(idUsuario: Long, gasto: GastoDTO, callback: (GastoDTO?, String?) -> Unit) {
        gastoService.registrarGasto(idUsuario, gasto).enqueue(object : Callback<GastoDTO> {
            override fun onResponse(call: Call<GastoDTO>, response: Response<GastoDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al registrar Gasto: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GastoDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun obtenerDineroDisponible(idUsuario: Long, callback: (Double?, String?) -> Unit) {
        gastoService.obtenerDineroDisponible(idUsuario,).enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
        }else {
                    callback(null, "Error al obtener dinero disponible: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<Double>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun obtenerGastosMesCategoria(idUsuario: Long, categoria: String, callback: (List<GastoDTO>?, String?) -> Unit) {
        gastoService.obtenerGastosMesCategoria(idUsuario, categoria).enqueue(object : Callback<List<GastoDTO>> {
            override fun onResponse(call: Call<List<GastoDTO>>, response: Response<List<GastoDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener gastos: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<GastoDTO>>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }


    fun obtenerValorGastosMesCategoria(idUsuario: Long, categoria: String, callback: (Double?, String?) -> Unit) {
        gastoService.obtenerValorGastosMesCategoria(idUsuario, categoria).enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener valor de gastos: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun obtenerValorGastosMes(idUsuario: Long, callback: (Double?, String?) -> Unit) {
        gastoService.obtenerValorGastosMes(idUsuario).enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener valor de gastos: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun listarGastosPorFechas(idUsuario: Long, fechaInicial: String, fechaFinal: String, callback: (List<GastoDTO>?, String?) -> Unit) {
        gastoService.listarGastosPorFechas(idUsuario, fechaInicial, fechaFinal).enqueue(object : Callback<List<GastoDTO>> {
            override fun onResponse(call: Call<List<GastoDTO>>, response: Response<List<GastoDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener gastos por fechas: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<GastoDTO>>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun listarGastosAscendentemente(idUsuario: Long, callback: (List<GastoDTO>?, String?) -> Unit) {
        gastoService.listarGastosAscendentemente(idUsuario).enqueue(object : Callback<List<GastoDTO>> {
            override fun onResponse(call: Call<List<GastoDTO>>, response: Response<List<GastoDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener gastos ascendentes: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<GastoDTO>>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }
    fun listarGastosAlto(idUsuario: Long, callback: (GastoDTO?, String?) -> Unit) {
        gastoService.listarGastosAlto(idUsuario).enqueue(object : Callback<GastoDTO> {
            override fun onResponse(call: Call<GastoDTO>, response: Response <GastoDTO> ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener gastos altos: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<GastoDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun listarGastosBajo(idUsuario: Long, callback: (GastoDTO?, String?) -> Unit) {
        gastoService.listarGastosBajo(idUsuario).enqueue(object : Callback<GastoDTO> {
            override fun onResponse(call: Call<GastoDTO>, response: Response<GastoDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener gastos bajos: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GastoDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun listarGastosDescendentemente(idUsuario: Long, callback: (List<GastoDTO>?, String?) -> Unit) {
        gastoService.listarGastosDescendentemente(idUsuario).enqueue(object : Callback<List<GastoDTO>> {
            override fun onResponse(call: Call<List<GastoDTO>>, response: Response<List<GastoDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener gastos descendentes: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<GastoDTO>>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun obtenerPromedioGastos(idUsuario: Long, callback: (Double?, String?) -> Unit) {
        gastoService.obtenerPromedioGastos(idUsuario).enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener promedio de gastos: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun obtenerGastoRecurrente(idUsuario: Long, callback: (String?, String?) -> Unit) {
        gastoService.obtenerGastoRecurrente(idUsuario).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener gasto recurrente: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun obtenerPorcentajeGastos(idUsuario: Long, callback: (Double?, String?) -> Unit) {
        gastoService.obtenerPorcentajeGastos(idUsuario).enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener porcentaje de gastos: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun obtenerPromedioDiario(idUsuario: Long, callback: (Double?, String?) -> Unit) {
        gastoService.obtenerPromedioDiario(idUsuario).enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener promedio diario de gastos: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun modificarGasto(idGasto: Long, gastoDTO: GastoDTO, callback: (GastoDTO?, String?) -> Unit) {
        gastoService.modificarGasto(idGasto, gastoDTO).enqueue(object : Callback<GastoDTO> {
            override fun onResponse(call: Call<GastoDTO>, response: Response<GastoDTO>) {
                if (response.isSuccessful) {
                    // Si la respuesta es exitosa, pasa el objeto GastoDTO modificado
                    callback(response.body(), null)
                } else {
                    // Si hay un error, pasa un mensaje de error con el código de respuesta
                    callback(null, "Error al modificar el gasto: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GastoDTO>, t: Throwable) {
                // Si hay un fallo en la conexión, pasa el mensaje de error
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun eliminarGasto(idGasto: Long, callback: (Boolean, String?) -> Unit) {
        gastoService.eliminarGasto(idGasto).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Si la eliminación es exitosa, devuelve true
                    callback(true, null)
                } else {
                    // Si hay un error, pasa un mensaje de error con el código de respuesta
                    callback(false, "Error al eliminar el gasto: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Si hay un fallo en la conexión, pasa el mensaje de error
                callback(false, "Fallo en la conexión: ${t.message}")
            }
        })
    }





}
