package com.practica.finazapp.Utils

import com.practica.finazapp.Entidades.ConsejosDTO
import retrofit2.Call
import retrofit2.http.GET

interface ConsejosService {

    @GET("run")
    fun obtenerConsejos(): Call<ConsejosDTO>
}