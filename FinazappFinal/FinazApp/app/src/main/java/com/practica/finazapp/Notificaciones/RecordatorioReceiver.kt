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
import com.practica.finazapp.Vista.Recordatorios_Usuario
import java.util.Date


class RecordatorioReceiver : BroadcastReceiver() {
    @SuppressLint("ScheduleExactAlarm")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val idRecordatorio = intent.getIntExtra("id_recordatorio", -1)
        val nombre = intent.getStringExtra("nombre") ?: "recordatorio"
        val valor = intent.getStringExtra("valor") ?: "un pago"
        val intervalo = intent.getLongExtra("intervalo", 0L)

        // Leer SharedPreferences para ver si la alarma fue cancelada
        val prefs = context.getSharedPreferences("RecordatoriosPrefs", Context.MODE_PRIVATE)
        val alarmaCancelada = prefs.getBoolean("alarma_cancelada_$idRecordatorio", false)

        if (alarmaCancelada) {
            Log.d("Recordatorios", "🚫 La alarma con ID $idRecordatorio ha sido cancelada, no se reprogramará.")
            return
        }

        // Mostrar notificación
        val mensaje = "No se te olvide pagar $valor a $nombre"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "recordatorio_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Recordatorios", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.principal)
            .setContentTitle("Recordatorio de Pago")
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())

        // ⚡ Reprogramar la alarma si tiene un intervalo válido y NO ha sido cancelada
        if (intervalo > 0) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val newIntent = Intent(context, RecordatorioReceiver::class.java).apply {
                putExtra("id_recordatorio", idRecordatorio)
                putExtra("nombre", nombre)
                putExtra("valor", valor)
                putExtra("intervalo", intervalo)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                idRecordatorio,
                newIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val siguienteAlarma = System.currentTimeMillis() + intervalo
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                siguienteAlarma,
                pendingIntent
            )

            Log.d("Recordatorios", "✅ Alarma reprogramada para $nombre en ${Date(siguienteAlarma)}")
        } else {
            Log.e("Recordatorios", "Intervalo no válido, la alarma no se reprogramará.")
        }
    }
}




