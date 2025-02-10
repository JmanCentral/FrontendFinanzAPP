package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.practica.finazapp.Entidades.RecordatorioDTO
import com.practica.finazapp.Notificaciones.RecordatorioReceiver
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.ReminderViewModel
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Recordatorios_Usuario : AppCompatActivity() , RecordatorioListener {

    private var usuarioId: Long = -1
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var recordatoriosViewModel: ReminderViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecordatorioAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recordatorios_usuario)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        recordatoriosViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)
        recyclerView = findViewById(R.id.recyclerViewRecordatorios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        usuarioId = intent.getLongExtra("usuarioId", -1)

        fetchRecordatorios()

        recordatoriosViewModel.operacionCompletada.observe(this) { completada ->
            if (completada == true) {
                fetchRecordatorios() // Actualizar la UI
            }
        }

        val btnNuevoRecordatorio = findViewById<ImageView>(R.id.NuevoRecordatorio)


        btnNuevoRecordatorio.setOnClickListener {
            Log.d("Recordatorio", "Botón Nuevo Recordatorio presionado")

            // Inflar el layout del diálogo
            val dialogView = layoutInflater.inflate(R.layout.dialog_nuevo_recordatorio, null)
            Log.d("Recordatorio", "Layout del diálogo inflado correctamente")

            // Obtener referencias de los elementos del diálogo
            val editTextNombre = dialogView.findViewById<TextInputEditText>(R.id.Busca_recordatorio)
            Log.d("Recordatorio", "Referencia a editTextNombre obtenida")
            val spinnerEstado = dialogView.findViewById<Spinner>(R.id.spinnerDescripcion1)
            Log.d("Recordatorio", "Referencia a spinnerEstado obtenida")
            val spinnerDias = dialogView.findViewById<Spinner>(R.id.spinnerDescripcion2)
            Log.d("Recordatorio", "Referencia a spinnerDias obtenida")
            val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha1)
            Log.d("Recordatorio", "Referencia a editTextFecha obtenida")
            val editTextCantidad =
                dialogView.findViewById<TextInputEditText>(R.id.editTextRecordatoriolimite)
            Log.d("Recordatorio", "Referencia a editTextCantidad obtenida")

            // Configurar el Spinner de estados
            val estados = listOf("Activo", "Pagado", "Vencido")
            val adapter = ArrayAdapter(this, R.layout.item_spinner_recordatorios, estados)
            adapter.setDropDownViewResource(R.layout.item_spinner_recordatorios)
            spinnerEstado.adapter = adapter
            Log.d("Recordatorio", "Spinner de estados configurado con: $estados")

            // Configurar el Spinner de días
            val diasOpciones = listOf("Cada 2 minutos", "Cada 3 minutos", "Cada 5 minutos")
            val adapterDias = ArrayAdapter(this, R.layout.item_spinner_dias, diasOpciones)
            adapterDias.setDropDownViewResource(R.layout.item_spinner_dias)
            spinnerDias.adapter = adapterDias
            Log.d("Recordatorio", "Spinner de días configurado con: $diasOpciones")

            // Configurar el selector de fecha
            editTextFecha.setOnClickListener {
                Log.d("Recordatorio", "Campo de fecha presionado, mostrando DatePicker")
                showDatePickerDialog(editTextFecha)
            }

            // Construir el diálogo
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Guardar") { _, _ ->
                    Log.d("Recordatorio", "Botón Guardar del diálogo presionado")
                    // Recoger los valores ingresados
                    val nombrerecordatorio = editTextNombre.text.toString().trim()
                    val estado = spinnerEstado.selectedItem.toString().trim()
                    val diasSeleccionado = spinnerDias.selectedItem.toString().trim()
                    val fechaOriginal = editTextFecha.text.toString().trim()
                    val limiteStr = editTextCantidad.text.toString().trim()

                    Log.d(
                        "Recordatorio",
                        "Valores ingresados - Nombre: '$nombrerecordatorio', Estado: '$estado', " +
                                "Días: '$diasSeleccionado', Fecha: '$fechaOriginal', Límite: '$limiteStr'"
                    )

                    // Validar que no haya campos vacíos
                    if (nombrerecordatorio.isBlank() || fechaOriginal.isBlank() || limiteStr.isBlank()) {
                        Log.e("Recordatorio", "Error: Se encontraron campos vacíos")
                        Toast.makeText(
                            this,
                            "Por favor, llene todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setPositiveButton
                    }

                    // Validar el formato de la fecha
                    val parts = fechaOriginal.split("/")
                    if (parts.size < 3) {
                        Log.e("Recordatorio", "Formato de fecha incorrecto: $fechaOriginal")
                        Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT)
                            .show()
                        return@setPositiveButton
                    }
                    val dia = parts[0].padStart(2, '0')
                    val mes = parts[1].padStart(2, '0')
                    val anio = parts[2]
                    val fecha = "${anio}-${mes}-${dia}"
                    Log.d("Recordatorio", "Fecha formateada: $fecha")

                    // Convertir la opción de días a milisegundos
                    val dias = when (diasSeleccionado) {
                        "Cada 2 minutos" -> 2 * 60 * 1000L  // 2 minutos en milisegundos
                        "Cada 3 minutos" -> 3 * 60 * 1000L  // 3 minutos en milisegundos
                        "Cada 5 minutos" -> 5 * 60 * 1000L  // 5 minutos en milisegundos
                        else -> {
                            Log.e("Recordatorio", "Opción de días no reconocida: $diasSeleccionado")
                            0L
                        }
                    }


                    // Convertir el límite a Double, manejando posibles errores
                    val valorDouble = try {
                        limiteStr.toDouble().also {
                            Log.d("Recordatorio", "Límite convertido a Double: $it")
                        }
                    } catch (e: NumberFormatException) {
                        Log.e("Recordatorio", "Error al convertir límite a Double: $limiteStr", e)
                        Toast.makeText(this, "Límite inválido", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    // Crear el objeto RecordatorioDTO
                    val nuevoRecordatorio = RecordatorioDTO(
                        id_recordatorio = 0,
                        nombre = nombrerecordatorio,
                        estado = estado,
                        dias_recordatorio = dias,
                        valor = valorDouble,
                        fecha = fecha
                    )
                    Log.d("Recordatorio", "RecordatorioDTO creado: $nuevoRecordatorio")

                    // Registrar el recordatorio mediante el ViewModel
                    recordatoriosViewModel.registrarRecordatorio(usuarioId, nuevoRecordatorio)
                    Log.d("Recordatorio", "Recordatorio enviado al ViewModel para su registro")
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    Log.d(
                        "Recordatorio",
                        "Botón Cancelar del diálogo presionado, se cierra el diálogo"
                    )
                    dialog.dismiss()
                }
                .create()

            // Mostrar el diálogo
            dialog.show()
            Log.d("Recordatorio", "Diálogo mostrado en pantalla")
        }
    }

    private fun fetchRecordatorios() {
        usuarioId = intent.getLongExtra("usuarioId", -1)
        Log.d("Recordatorios", "Usuario ID recibido: $usuarioId")

        recordatoriosViewModel.listarRecordatorios(usuarioId)
        Log.d("Recordatorios", "Llamado a listarRecordatorios()")

        recordatoriosViewModel.recordatoriosLiveData.observe(this) { recordatorioslist ->
            if (recordatorioslist != null) {
                Log.d("Recordatorios", "Lista de recordatorios recibida: ${recordatorioslist.size} elementos")

                adapter = RecordatorioAdapter(recordatorioslist)
                adapter.setOnItemClickListener3(this)
                recyclerView.adapter = adapter

                // Programar alarmas para cada recordatorio
                for (recordatorio in recordatorioslist) {
                    Log.d("Recordatorios", "Programando alarma para: ${recordatorio.nombre}")
                    programarAlarma(recordatorio)
                }
            } else {
                Log.w("Recordatorios", "No se encontraron recordatorios")
                Toast.makeText(this, "No se encontraron recordatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun programarAlarma(recordatorio: RecordatorioDTO) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, RecordatorioReceiver::class.java).apply {
            putExtra("nombre", recordatorio.nombre)
            putExtra("intervalo", recordatorio.dias_recordatorio) // Guardar intervalo en Intent
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            recordatorio.id_recordatorio.toInt(), // ID único para cada recordatorio
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intentar parsear la fecha en diferentes formatos
        val formatosFecha = listOf("yyyy-MM-dd", "dd/MM/yyyy")
        var fecha: Long? = null
        for (formato in formatosFecha) {
            try {
                fecha = SimpleDateFormat(formato, Locale.getDefault()).parse(recordatorio.fecha)?.time
                if (fecha != null) break // Salir del loop si se parsea correctamente
            } catch (e: Exception) {
                Log.e("Recordatorios", "Error al parsear la fecha con formato $formato para ${recordatorio.nombre}: ${e.message}")
            }
        }

        if (fecha == null) {
            Log.e("Recordatorios", "No se pudo parsear la fecha para ${recordatorio.nombre}, alarma no programada.")
            return
        }

        // Asegurar que el intervalo es válido (en milisegundos)
        val intervalo = recordatorio.dias_recordatorio
        if (intervalo <= 0) {
            Log.e("Recordatorios", "Intervalo de repetición inválido ($intervalo ms). La alarma no se programará.")
            return
        }

        // Configurar la alarma según la versión de Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                fecha,
                pendingIntent
            )
            Log.d("Recordatorios", "Alarma programada con setExactAndAllowWhileIdle para ${recordatorio.nombre} a las ${Date(fecha)}")
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                fecha,
                intervalo,
                pendingIntent
            )
            Log.d("Recordatorios", "Alarma programada con setRepeating para ${recordatorio.nombre} a las ${Date(fecha)} cada ${intervalo / 1000} segundos")
        }
    }






    override fun onItemClick3(recordatorios: RecordatorioDTO) {

        /*
        Log.d("onItemClick", "Recordatorio clickeado: ${recordatorios.nombre}")

        val dialogView = layoutInflater.inflate(R.layout.dialog_modificar_recordatorio, null)
        val editTextNombre = dialogView.findViewById<TextInputEditText>(R.id.Busca_recordatorio1)
        val spinnerEstado = dialogView.findViewById<Spinner>(R.id.spinnerDescripcion3)
        val spinnerDias = dialogView.findViewById<Spinner>(R.id.spinnerDescripcion4)
        val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
        val editTextCantidad = dialogView.findViewById<TextInputEditText>(R.id.editTextRecordatorio1limite)
        val btnEliminar = dialogView.findViewById<Button>(R.id.button) // Asegurar que sea el ID correcto en el layout

        // Llenar los campos con la información actual del recordatorio
        editTextNombre.setText(recordatorios.nombre)
        editTextCantidad.setText(recordatorios.valor.toString())

        // Convertir la fecha de "yyyy-MM-dd" a "dd/MM/yyyy"
        val parts = recordatorios.fecha.split("-")
        val fechaFormateada = "${parts[2]}/${parts[1]}/${parts[0]}"
        editTextFecha.setText(fechaFormateada)

        // Configurar el Spinner de estado con la opción actual
        val estados = listOf("Activo", "Pagado", "Vencido")
        val adapterEstado = ArrayAdapter(this, R.layout.item_spinner_recordatorios, estados)
        adapterEstado.setDropDownViewResource(R.layout.item_spinner_recordatorios)
        spinnerEstado.adapter = adapterEstado
        spinnerEstado.setSelection(estados.indexOf(recordatorios.estado))

        // Configurar el Spinner de días con la opción actual
        val diasOpciones = listOf("3 dias", "7 dias", "15 dias", "1 mes", "3 meses", "6 meses")
        val adapterDias = ArrayAdapter(this, R.layout.item_spinner_dias, diasOpciones)
        adapterDias.setDropDownViewResource(R.layout.item_spinner_dias)
        spinnerDias.adapter = adapterDias
        val diasIndex = when (recordatorios.dia) {
            3 -> 0
            7 -> 1
            15 -> 2
            30 -> 3
            90 -> 4
            180 -> 5
            else -> -1
        }
        if (diasIndex != -1) spinnerDias.setSelection(diasIndex)

        editTextFecha.setOnClickListener {
            showDatePickerDialog(editTextFecha)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombreNuevo = editTextNombre.text.toString().trim()
                val estadoNuevo = spinnerEstado.selectedItem.toString().trim()
                val diasSeleccionado = spinnerDias.selectedItem.toString().trim()
                val fechaOriginal = editTextFecha.text.toString().trim()
                val limiteStr = editTextCantidad.text.toString().trim()

                if (nombreNuevo.isBlank() || fechaOriginal.isBlank() || limiteStr.isBlank()) {
                    Toast.makeText(
                        this,
                        "Por favor, llene todos los campos",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                // Convertir la fecha de "dd/MM/yyyy" a "yyyy-MM-dd"
                val partes = fechaOriginal.split("/")
                val dia = partes[0].padStart(2, '0')
                val mes = partes[1].padStart(2, '0')
                val anio = partes[2]
                val fechaNueva = "${anio}-${mes}-${dia}"

                // Convertir el texto del Spinner a un número entero
                val dias = when (diasSeleccionado) {
                    "3 dias" -> 3
                    "7 dias" -> 7
                    "15 dias" -> 15
                    "1 mes" -> 30
                    "3 meses" -> 90
                    "6 meses" -> 180
                    else -> 0  // En caso de error
                }

                val recordatorioActualizado = RecordatorioDTO(
                    id_recordatorio = recordatorios.id_recordatorio,
                    nombre = nombreNuevo,
                    estado = estadoNuevo,
                    dia = dias,
                    valor = limiteStr.toDouble(),
                    fecha = fechaNueva
                )

                recordatoriosViewModel.modificarRecordatorio(recordatorios.id_recordatorio, recordatorioActualizado)


            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        btnEliminar.setOnClickListener {
            recordatoriosViewModel.eliminarRecordatorio(recordatorios.id_recordatorio)

            dialog.dismiss()
        }

        dialog.show()

         */
    }





    private fun convertirFecha(fechaOriginal: String): String {
        // Convierte la fecha de formato "dd/MM/yyyy" a "yyyy-MM-dd"
        val parts = fechaOriginal.split("/")
        val dia = parts[0].padStart(2, '0')
        val mes = parts[1].padStart(2, '0')
        val anio = parts[2]
        return "$anio-$mes-$dia"
    }

    private fun showDatePickerDialog(editTextFecha: EditText) {
        // Obtener la fecha actual
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Crear y mostrar el DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year1, monthOfYear, dayOfMonth1 ->
                val fechaSeleccionada = "$dayOfMonth1/${monthOfYear + 1}/$year1"
                editTextFecha.setText(fechaSeleccionada)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }


}