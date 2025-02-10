package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
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
import com.practica.finazapp.Notificaciones.AlarmaReceiver
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.ReminderViewModel
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import java.text.SimpleDateFormat
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
        Alarma ()

        recordatoriosViewModel.operacionCompletada.observe(this) { completada ->
            if (completada == true) {
                fetchRecordatorios() // Actualizar la UI
                Alarma ()
            }
        }

        val btnNuevoRecordatorio = findViewById<ImageView>(R.id.NuevoRecordatorio)


        btnNuevoRecordatorio.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_nuevo_recordatorio, null)
            val editTextNombre = dialogView.findViewById<TextInputEditText>(R.id.Busca_recordatorio)
            val spinnerEstado = dialogView.findViewById<Spinner>(R.id.spinnerDescripcion1)
            val spinnerDias = dialogView.findViewById<Spinner>(R.id.spinnerDescripcion2)
            val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha1)
            val editTextCantidad = dialogView.findViewById<TextInputEditText>(R.id.editTextRecordatoriolimite)

            // Configurar el Spinner con las opciones de estados
            val estados = listOf("Activo", "Pagado", "Vencido")
            val adapter = ArrayAdapter(this, R.layout.item_spinner_recordatorios, estados)
            adapter.setDropDownViewResource(R.layout.item_spinner_recordatorios)
            spinnerEstado.adapter = adapter

            // Configurar el Spinner con las opciones de días
            val diasOpciones = listOf( "Cada 2 minutos", "Cada 3 minutos", "Cada 5 minutos")
            val adapterDias = ArrayAdapter(this, R.layout.item_spinner_dias, diasOpciones)
            adapterDias.setDropDownViewResource(R.layout.item_spinner_dias)
            spinnerDias.adapter = adapterDias

            editTextFecha.setOnClickListener {
                showDatePickerDialog(editTextFecha)
            }

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Guardar") { _, _ ->
                    val nombrerecordatorio = editTextNombre.text.toString().trim()
                    val estado = spinnerEstado.selectedItem.toString().trim()
                    val diasSeleccionado = spinnerDias.selectedItem.toString().trim()
                    val fechaOriginal = editTextFecha.text.toString().trim()
                    val limiteStr = editTextCantidad.text.toString().trim()

                    if (nombrerecordatorio.isBlank() || fechaOriginal.isBlank() || limiteStr.isBlank()) {
                        Toast.makeText(
                            this,
                            "Por favor, llene todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setPositiveButton
                    }

                    val parts = fechaOriginal.split("/")
                    val dia = parts[0].padStart(2, '0')
                    val mes = parts[1].padStart(2, '0')
                    val anio = parts[2]
                    val fecha = "${anio}-${mes}-${dia}"

                    val dias = when (diasSeleccionado) {
                        "Cada 2 minutos" -> 120000
                        "Cada 3 minutos" -> 180000
                        "Cada 5 minutos" -> 300000
                        else -> 0  // En caso de error
                    }

                    val nuevoRecordatorio = RecordatorioDTO(
                        id_recordatorio = 0,
                        nombre = nombrerecordatorio,
                        estado = estado,
                        dia = dias,
                        valor = limiteStr.toDouble(),
                        fecha = fecha
                    )

                    Log.d("NuevoRecordatorio", "Recordatorio creado: $dias")

                    recordatoriosViewModel.registrarRecordatorio(usuarioId, nuevoRecordatorio)

                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            dialog.show()
        }

    }

    private fun fetchRecordatorios() {

        usuarioId = intent.getLongExtra("usuarioId", -1)
        recordatoriosViewModel.listarRecordatorios(usuarioId)
        recordatoriosViewModel.recordatoriosLiveData.observe(this) { recordatorioslist ->
            if (recordatorioslist != null) {
                adapter = RecordatorioAdapter(recordatorioslist)
                adapter.setOnItemClickListener3(this) // Pasamos la Activity como listener
                recyclerView.adapter = adapter
            } else {
                Toast.makeText(this, "No se encontraron recordatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun Alarma () {

        recordatoriosViewModel.listarRecordatorios(usuarioId)
        recordatoriosViewModel.recordatoriosLiveData.observe(this) { recordatorios ->
            recordatorios?.forEach { recordatorio ->
                programarAlarma(this, recordatorio)
            }
        }

    }

    fun programarAlarma(context: Context, recordatorio: RecordatorioDTO) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmaReceiver::class.java).apply {
            putExtra("nombre", recordatorio.nombre)
            putExtra("valor", recordatorio.valor)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            recordatorio.id_recordatorio.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Convertir la fecha del recordatorio a milisegundos
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaFinal = formato.parse(recordatorio.fecha)?.time ?: return

        val ahora = System.currentTimeMillis()

        // Asegurar que el valor de 'dia' sea Long
        val intervaloRepeticion = if (recordatorio.dia > 0) {
            recordatorio.dia.toLong() * AlarmManager.INTERVAL_DAY
        } else {
            AlarmManager.INTERVAL_DAY
        }

        // Determinar cuándo debe ejecutarse la primera vez
        val tiempoEjecucion = if (fechaFinal > ahora) fechaFinal else ahora + intervaloRepeticion

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            tiempoEjecucion,  // Ahora sí usamos la fecha corregida
            intervaloRepeticion,  // Se repite correctamente
            pendingIntent
        )
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