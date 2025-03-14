package com.practica.finazapp.RepositoriosApiRest

import android.content.Context
import com.practica.finazapp.Entidades.DepositoDTO
import com.practica.finazapp.Utils.Cliente
import com.practica.finazapp.Utils.DepositoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DepositoRepository(context: Context) {

    private val depositoService: DepositoService by lazy {
        Cliente.getCliente("http://100.106.18.126:8862/Finanzapp/Deposito/", context)
            .create(DepositoService::class.java)
    }

    fun registrarDeposito(depositoDTO: DepositoDTO, idUsuario: Long, idAlcancia: Long, callback: (DepositoDTO?, String?) -> Unit) {
        depositoService.registrarDeposito(depositoDTO, idUsuario, idAlcancia).enqueue(object : Callback<DepositoDTO> {
            override fun onResponse(call: Call<DepositoDTO>, response: Response<DepositoDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error en la solicitud: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DepositoDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun obtenerDepositos(idAlcancia: Long, callback: (List<DepositoDTO>?, String?) -> Unit) {
        depositoService.obtenerDepositos(idAlcancia).enqueue(object : Callback<List<DepositoDTO>> {
            override fun onResponse(call: Call<List<DepositoDTO>>, response: Response<List<DepositoDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener depósitos: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<DepositoDTO>>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun obtenerValorGastosMesDeposito(idUsuario: Long, callback: (Double?, String?) -> Unit) {
        depositoService.obtenerValorGastosMesDeposito(idUsuario).enqueue(object : Callback<Double> {
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

    fun modificarDepositos(depositoDTO: DepositoDTO, idDeposito: Long, idAlcancia: Long, callback: (DepositoDTO?, String?) -> Unit) {
        depositoService.modificarDepositos(depositoDTO, idDeposito, idAlcancia).enqueue(object : Callback<DepositoDTO> {
            override fun onResponse(call: Call<DepositoDTO>, response: Response<DepositoDTO>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                    } else {
                    callback(null, "Error al modificar Alerta: ${response.code()}")
                }
                }
            override fun onFailure(call: Call<DepositoDTO>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })

    }

    fun eliminarDepositos(idDeposito: Long, idAlcanica: Long, callback: (Boolean, String?) -> Unit) {
        depositoService.eliminarDepositos(idDeposito , idAlcanica).enqueue(object : Callback<Void> {
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
