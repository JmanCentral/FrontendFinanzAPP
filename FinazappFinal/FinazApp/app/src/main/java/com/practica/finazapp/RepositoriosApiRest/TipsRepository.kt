package com.practica.finazapp.RepositoriosApiRest

import android.content.Context
import com.practica.finazapp.Entidades.GastoDTO
import com.practica.finazapp.Entidades.TipsDTO
import com.practica.finazapp.Utils.Cliente
import com.practica.finazapp.Utils.GastoService
import com.practica.finazapp.Utils.TipsService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TipsRepository (context: Context) {

    private val tipsService: TipsService by lazy {

        Cliente.getCliente("http://100.96.16.104:8862/Finanzapp/Tips/", context)
            .create(TipsService::class.java)
    }

    fun obteherTips(idUsuario: Long, callback: (List<TipsDTO>?, String?) -> Unit) {
        tipsService.obtenerTips(idUsuario).enqueue(object : Callback<List<TipsDTO>> {
            override fun onResponse(call: Call<List<TipsDTO>>, response: Response<List<TipsDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al registrar Gasto: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<TipsDTO>>, t: Throwable) {
                callback(null, "Fallo en la conexión: ${t.message}")
            }
        })
    }
}