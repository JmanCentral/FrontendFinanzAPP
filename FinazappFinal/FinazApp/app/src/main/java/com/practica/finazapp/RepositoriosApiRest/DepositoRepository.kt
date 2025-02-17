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
        Cliente.getCliente("http://192.168.10.3:8862/Finanzapp/Deposito/", context)
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
}
