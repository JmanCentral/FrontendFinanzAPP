package com.practica.finazapp.Utils

import android.content.Context
import android.content.Intent
import com.practica.finazapp.Vista.Login
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(private val context: Context) : Interceptor {

    private val sharedPreferences = context.getSharedPreferences("MiApp", Context.MODE_PRIVATE)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sharedPreferences.getString("TOKEN", null)
        val request = chain.request().newBuilder()

        // Añadir el token a la cabecera si está disponible
        if (!token.isNullOrEmpty()) {
            request.addHeader("Authorization", "Bearer $token")
        }

        // Realizar la solicitud
        val response = chain.proceed(request.build())

        // Verificar si el token ha expirado (código 401 o 403)
        if (response.code == 401 || response.code == 403) {
            // Limpiar el token y redirigir al usuario a la pantalla de login
            limpiarTokenYRedirigirALogin()
        }

        return response
    }

    private fun limpiarTokenYRedirigirALogin() {
        // Limpiar el token en SharedPreferences
        with(sharedPreferences.edit()) {
            remove("TOKEN")
            apply()
        }

        // Redirigir al usuario a la pantalla de login
        val intent = Intent(context, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}
