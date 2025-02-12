package com.practica.finazapp.Notificaciones

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.practica.finazapp.R
import java.util.Date


class RecordatorioReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val nombre = intent.getStringExtra("nombre") ?: "recordatorio"
        val valor = intent.getStringExtra("valor") ?: "un pago"
        val intervalo = intent.getLongExtra("intervalo", 0L) // Intervalo en milisegundos

        val mensaje = "No se te olvide pagar $valor a $nombre"

        // Crear la notificación
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "recordatorio_channel"

        // Crear el canal de notificación (Solo necesario en Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Recordatorios", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Construir la notificación
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)  // Asegúrate de tener un ícono válido
            .setContentTitle("Recordatorio de Pago")
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Mostrar la notificación
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())

        // ⚡ REPROGRAMAR LA ALARMA PARA QUE SE REPITA ⚡
        if (intervalo > 0) {
            reprogramarAlarma(context, nombre, valor, intervalo)
        } else {
            Log.e("Recordatorios", "Intervalo no válido, la alarma no se reprogramará.")
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun reprogramarAlarma(context: Context, nombre: String, valor: String, intervalo: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val newIntent = Intent(context, RecordatorioReceiver::class.java).apply {
            putExtra("nombre", nombre)
            putExtra("valor", valor)
            putExtra("intervalo", intervalo)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            nombre.hashCode(), // ID único para el recordatorio
            newIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val siguienteAlarma = System.currentTimeMillis() + intervalo

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            siguienteAlarma,
            pendingIntent
        )

        Log.d("Recordatorios", "✅ Alarma reprogramada para ${Date(siguienteAlarma)} con intervalo de ${intervalo / 1000} segundos")
    }
}

