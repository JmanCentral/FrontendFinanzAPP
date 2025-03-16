package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.practica.finazapp.Entidades.AlertaDTO
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.AlertViewModel
import com.practica.finazapp.ViewModelsApiRest.IncomeViewModel
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.ViewModelsApiRest.SpendViewModel
import com.practica.finazapp.databinding.FragmentAlertasBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import kotlin.math.log

class FragmentAlertas : Fragment(), AlertasListener {

    private var usuarioId: Long = -1
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var ingresoViewModel: IncomeViewModel
    private lateinit var gastosViewModel: SpendViewModel
    private lateinit var alertaViewModel: AlertViewModel

    private var _binding: FragmentAlertasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertasBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ingresoViewModel = ViewModelProvider(this)[IncomeViewModel::class.java]
        gastosViewModel = ViewModelProvider(this)[SpendViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        alertaViewModel = ViewModelProvider(this)[AlertViewModel::class.java]

        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { usuarioId ->
            Log.d("FragmentAlertas", "id usuario: $usuarioId")
            usuarioId?.let {
                this.usuarioId = it
                cargarAlertas()  // Cargar alertas existentes
            }
        }

        alertaViewModel.operacionCompletada.observe(viewLifecycleOwner) { completada ->
            if (completada == true) {
                cargarAlertas()// Actualizar la UI
            }
        }

        val btnNuevaAlerta = binding.btnalertanueva

        btnNuevaAlerta.setOnClickListener {
            mostrarDialogoNuevaAlerta()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    private fun mostrarDialogoNuevaAlerta() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_nueva_alerta, null)
        val editTextNombreAlarma = dialogView.findViewById<TextInputEditText>(R.id.editTextNombreAlarma)
        val spinnerDescripcion = dialogView.findViewById<Spinner>(R.id.spinnerDescripcion)
        val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
        val editTextAlertalimite = dialogView.findViewById<EditText>(R.id.editTextAlertalimite)

        // Configurar el Spinner con las opciones de categorías
        val categorias = listOf("disponible", "Gastos Hormiga", "Alimentos", "Transporte", "Servicios", "Mercado", "Deudas")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, categorias)
        adapter.setDropDownViewResource(R.layout.item_spinner)
        spinnerDescripcion.adapter = adapter

        editTextFecha.setOnClickListener {
            showDatePickerDialog(editTextFecha)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Guardar", null) // Se asignará más tarde para no cerrar el diálogo automáticamente
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.show()

        // Sobrescribir el comportamiento del botón "Guardar"
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nombreAlarma = editTextNombreAlarma.text.toString().trim()
            val descripcion = spinnerDescripcion.selectedItem.toString().trim()
            val fechaOriginal = editTextFecha.text.toString().trim()
            val limiteStr = editTextAlertalimite.text.toString().trim()

            if (nombreAlarma.isBlank() || fechaOriginal.isBlank() || limiteStr.isBlank()) {
                // Mostrar alerta sin cerrar el diálogo principal
                AlertDialog.Builder(requireContext())
                    .setTitle("Campos vacíos")
                    .setMessage("Por favor, llene todos los campos antes de guardar.")
                    .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                    .create()
                    .show()
                return@setOnClickListener
            }

            val limite: Double
            try {
                limite = limiteStr.toDouble()
            } catch (e: NumberFormatException) {
                // Mostrar alerta si el límite no es válido
                AlertDialog.Builder(requireContext())
                    .setTitle("Valor inválido")
                    .setMessage("Ingrese un valor numérico válido para el límite.")
                    .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                    .create()
                    .show()
                return@setOnClickListener
            }

            val fecha = convertirFecha(fechaOriginal)

            // Obtener el ingreso total del mes y proceder con la comparación
            ingresoViewModel.obtenerTotalIngresos(usuarioId)
            ingresoViewModel.totalIngresosLiveData.observeOnce(viewLifecycleOwner) { ingresoTotal ->
                ingresoTotal?.let {
                    if (limite > it) {
                        // Mostrar alerta si el límite supera el ingreso total
                        AlertDialog.Builder(requireContext())
                            .setTitle("Límite excedido")
                            .setMessage("El valor de la alerta supera el ingreso total.")
                            .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                            .create()
                            .show()
                    } else {
                        // Guardar la nueva alerta si no excede el ingreso total
                        val nuevaAlerta = AlertaDTO(
                            id_alerta = 0,
                            nombre = nombreAlarma,
                            descripcion = descripcion,
                            fecha = fecha,
                            valor = limite
                        )
                        alertaViewModel.registrarAlerta(usuarioId, nuevaAlerta)
                        Toast.makeText(requireContext(), "Alerta guardada correctamente.", Toast.LENGTH_SHORT).show()
                        cargarAlertas()
                        dialog.dismiss() // Cerrar el diálogo principal después del guardado exitoso
                    }
                } ?: run {
                    // Mostrar alerta si no se pudo obtener el ingreso total
                    AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage("No se pudo obtener el ingreso total.")
                        .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                        .create()
                        .show()
                }
            }
        }
    }

    private fun convertirFecha(fechaOriginal: String): String {
        // Convierte la fecha de formato "dd/MM/yyyy" a "yyyy-MM-dd"
        val parts = fechaOriginal.split("/")
        val dia = parts[0].padStart(2, '0')
        val mes = parts[1].padStart(2, '0')
        val anio = parts[2]
        return "$anio-$mes-$dia"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onAlertasCargadas(alertas: List<AlertaDTO>, ingresoTotal: Double) {


            // Limpiar el contenedor antes de cargar nuevas alertas
            binding.contenedorAlerta.removeAllViews()

            for (alerta in alertas) {
                // Crear un TextView o algún componente visual para mostrar cada alerta
                val alertaView = TextView(requireContext()).apply {
                    text = "Alerta: ${alerta.nombre} - Límite: ${alerta.valor} - Fecha: ${alerta.fecha}"
                    textSize = 16f
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.negro))
                }

                // Verificar si el valor de la alerta supera el ingreso total
                if (alerta.valor > ingresoTotal) {
                    alertaView.setTextColor(ContextCompat.getColor(requireContext(), R.color.rojo)) // Mostrar en rojo si excede el límite
                    // Evitar múltiples Toasts para cada alerta excedida
                    // Puedes optar por mostrar un único Toast fuera del ciclo si hay al menos una alerta excedida
                }

                cargarAlertas(alertas, binding.contenedorAlerta)
            }

            // Opcional: Mostrar un Toast general si hay alguna alerta que excede el ingreso total
            val hayExceso = alertas.any { it.valor > ingresoTotal }
            if (hayExceso) {
                Toast.makeText(requireContext(), "Hay alertas que exceden tu ingreso total.", Toast.LENGTH_LONG).show()
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun cargarAlertas() {
        // Obtener los ingresos totales del mes del usuario
        Log.d("FragmentAlertas", "Cargando alertas")
        ingresoViewModel.obtenerTotalIngresos(usuarioId)
        ingresoViewModel.totalIngresosLiveData.observe(viewLifecycleOwner) { ingresoTotal ->
            // Si el ingreso total es null, significa que no hay ingresos para el mes actual
            if (ingresoTotal == null) {
                Toast.makeText(requireContext(), "No hay ingresos registrados para este mes.", Toast.LENGTH_SHORT).show()
                binding.contenedorAlerta.removeAllViews()
                return@observe
            }
            // Obtener las alertas del usuario actual
            alertaViewModel.obtenerAlertaPorMes(usuarioId)
            alertaViewModel.alertasPorMesLiveData.observe(viewLifecycleOwner) { alertas ->
                // Notificar al listener con las alertas y el ingreso total
                if (alertas != null) {
                    Log.d("FragmentAlertas", "Alertas: $alertas")
                    onAlertasCargadas(alertas, ingresoTotal)
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun cargarAlertas(alertas: List<AlertaDTO>, contenedor: ViewGroup) {
        Log.d("DashboardFragment", "Cargando alertas en contenedor ${contenedor.id}")
        contenedor.removeAllViews()
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 2

        for (alerta in alertas) {
            val descripcionTextView = TextView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                text = alerta.nombre
                setTextAppearance(R.style.TxtNegroMedianoItalic)
            }

            val valorTextView = TextView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                text = "${numberFormat.format(alerta.valor)}$"
                setTextAppearance(R.style.TxtNegroMedianoItalic)
            }

            val registroLayout = ConstraintLayout(requireContext()).apply {
                layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                addView(descripcionTextView)
                addView(valorTextView)

                val descParams = descripcionTextView.layoutParams as ConstraintLayout.LayoutParams
                descParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                descParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID

                val valorParams = valorTextView.layoutParams as ConstraintLayout.LayoutParams
                valorParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                valorParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID

                // Agregar OnClickListener para abrir el diálogo de modificación de alerta
                setOnClickListener {
                    val dialogView = layoutInflater.inflate(R.layout.dialog_modificar_alerta, null)
                    val textViewTitulo = dialogView.findViewById<TextView>(R.id.titulo)
                    val btnEliminarAlerta = dialogView.findViewById<Button>(R.id.btnEliminarAlerta)
                    val editTextValor = dialogView.findViewById<TextInputEditText>(R.id.editTextIngresoLimite)
                    val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)

                    val dialogModificarAlerta = AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .setPositiveButton("Aceptar", null) // Se asignará más tarde para no cerrar el diálogo automáticamente
                        .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                        .create()

                    textViewTitulo.text = "Modificar alerta '${alerta.nombre}'"
                    val fecha = alerta.fecha
                    val parts = fecha.split("-")
                    val fechaFormateada = "${parts[2]}/${parts[1]}/${parts[0]}"
                    editTextFecha.setText(fechaFormateada)
                    editTextFecha.setOnClickListener {
                        showDatePickerDialog(editTextFecha)
                    }
                    editTextValor.setText(alerta.valor.toString())

                    btnEliminarAlerta.setOnClickListener {
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                alertaViewModel.eliminarAlerta(alerta.id_alerta)
                            }
                        }
                        dialogModificarAlerta.dismiss()
                    }

                    dialogModificarAlerta.show()

                    // Sobrescribir el comportamiento del botón "Aceptar"
                    dialogModificarAlerta.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val valor = editTextValor.text.toString()
                        val fecha = editTextFecha.text.toString()

                        if (fecha.isBlank() || valor.isBlank()) {
                            // Mostrar alerta sin cerrar el diálogo principal
                            AlertDialog.Builder(requireContext())
                                .setTitle("Campos vacíos")
                                .setMessage("Por favor, llene todos los campos antes de guardar.")
                                .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                                .create()
                                .show()
                            return@setOnClickListener
                        }

                        val valorDouble = valor.toDoubleOrNull()
                        if (valorDouble == null) {
                            // Mostrar alerta si el valor no es válido
                            AlertDialog.Builder(requireContext())
                                .setTitle("Valor inválido")
                                .setMessage("Ingrese un valor numérico válido.")
                                .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                                .create()
                                .show()
                            return@setOnClickListener
                        }

                        // Formatear la fecha
                        val partsFecha = fecha.split("/")
                        val dia = partsFecha[0].padStart(2, '0')
                        val mes = partsFecha[1].padStart(2, '0')
                        val anio = partsFecha[2]
                        val fechaFormateadaModificada = "${anio}-${mes}-${dia}"

                        // Crear la alerta modificada
                        val alertaModificada = alerta.copy(
                            valor = valorDouble,
                            fecha = fechaFormateadaModificada
                        )

                        // Modificar la alerta
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                alertaViewModel.modificarAlerta(idAlerta = alerta.id_alerta, alertaDTO = alertaModificada)
                            }
                        }
                        dialogModificarAlerta.dismiss() // Cerrar el diálogo principal después del guardado exitoso
                    }
                }
            }

            contenedor.addView(registroLayout)
        }
    }

    private fun showDatePickerDialog(editTextFecha: EditText) {
        // Obtener la fecha actual
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Crear y mostrar el DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
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

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(value: T) {
                removeObserver(this) // Elimina el observador después de la primera actualización
                observer.onChanged(value)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


