package com.practica.finazapp.Vista

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import androidx.activity.ComponentActivity
import com.practica.finazapp.R
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Crear el canal de notificaciones
        crearCanalNotificacion(this)

        // Verificar si hay un token válido
        val token = getToken()
        if (token != null) {
            // Redirigir a la Activity principal (Dashboard)
            startActivity(Intent(this, Dashboard::class.java))
        } else {
            // Redirigir a la pantalla de inicio de sesión (Login)
            startActivity(Intent(this, Login::class.java))
        }

        // Cerrar la MainActivity
        finish()
    }

    private fun getToken(): String? {
        val sharedPreferences = getSharedPreferences("MiApp", Context.MODE_PRIVATE)
        return sharedPreferences.getString("TOKEN", null)
    }


    private fun crearCanalNotificacion(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "CanalGastosAltos"
            val descripcion = "Notificaciones para gastos altos"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel("GASTOS_ALTOS", nombre, importancia)
            canal.description = descripcion

            val notificationManager: NotificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }
}