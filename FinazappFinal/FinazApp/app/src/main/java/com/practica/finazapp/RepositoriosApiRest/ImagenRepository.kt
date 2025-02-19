package com.practica.finazapp.RepositoriosApiRest

import android.content.Context
import com.practica.finazapp.Utils.Cliente
import com.practica.finazapp.Utils.ImagenService


class ImagenRepository (context: Context) {
    private val imagenService: ImagenService by lazy {
        Cliente.getCliente("http://192.168.10.3:8862/Finanzapp/api/", context)
            .create(imagenService::class.java)
    }
}