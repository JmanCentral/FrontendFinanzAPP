package com.practica.finazapp.Notificaciones

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RecordatorioWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val fechaRecordatorio = inputData.getString("fecha_recordatorio") ?: return Result.failure()
        val dias = inputData.getInt("dias", 0)

        val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaActual = Calendar.getInstance()
        val fechaRecordatorioCalendar = Calendar.getInstance()

        try {
            fechaRecordatorioCalendar.time = formatoFecha.parse(fechaRecordatorio) ?: return Result.failure()
        } catch (e: Exception) {
            Log.e("RecordatorioWorker", "Error al parsear la fecha", e)
            return Result.failure()
        }

        fechaRecordatorioCalendar.add(Calendar.DAY_OF_YEAR, -dias)

        if (fechaActual.timeInMillis >= fechaRecordatorioCalendar.timeInMillis) {
            mostrarNotificacion()
        }

        return Result.success()
    }

    private fun mostrarNotificacion() {
        Log.d("RecordatorioWorker", "¡Es hora del recordatorio!")
        // Aquí puedes implementar una notificación push usando NotificationManager
    }
}