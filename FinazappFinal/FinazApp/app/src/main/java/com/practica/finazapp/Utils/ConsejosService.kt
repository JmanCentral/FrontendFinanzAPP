package com.practica.finazapp.Utils

import com.practica.finazapp.Entidades.ConsejosDTO
import com.practica.finazapp.Entidades.GastoDTO
import com.practica.finazapp.Entidades.TipsDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ConsejosService {

    @GET("Obtener")
    fun obtenerConsejos(): Call<List<ConsejosDTO>>

}