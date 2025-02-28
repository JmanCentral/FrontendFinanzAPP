package com.practica.finazapp.Vista

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.example.finanzapp.viewmodels.AlcanciaViewModel
import com.google.android.material.textfield.TextInputEditText
import com.practica.finazapp.Entidades.AlcanciaDTO
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.databinding.FragmentGraficosAvanzadosBinding
import java.util.Calendar


class Graficos_Avanzados : Fragment(), AlcanciaListener {

    private var usuarioId: Long = -1
    private var isFirstLoad = true
    private var _binding: FragmentGraficosAvanzadosBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var alcanciaViewModel: AlcanciaViewModel
    private lateinit var alcanciaAdapter: AlcanciaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraficosAvanzadosBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        alcanciaViewModel = ViewModelProvider(this).get(AlcanciaViewModel::class.java)

        // Configurar RecyclerView
        binding.recyclerViewAlcancias.layoutManager = LinearLayoutManager(requireContext())
        alcanciaAdapter = AlcanciaAdapter(emptyList()) // Inicializar con lista vacía
        binding.recyclerViewAlcancias.adapter = alcanciaAdapter

        Log.d("Alcancia", "onViewCreated() llamado")
        // Observar el ID de usuario y cargar alcancías
        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { id ->
            Log.d("Alcancia", "Usuario ID observado: $id")
            usuarioId = id
            obtenerAlcancias(id)
            binding.RegistrarAlcancia.setOnClickListener {
                registrar(id)
            }
        }

        alcanciaViewModel.operacionCompletadaLiveData.observe(viewLifecycleOwner) { completada ->
            if (completada == true) {
                obtenerAlcancias(usuarioId , isReload = true)  // Usa usuarioId directamente
            }
        }
    }

    private fun obtenerAlcancias(usuarioId: Long, isReload: Boolean = false) {
        alcanciaViewModel.obtenerAlcanciasPorUser(usuarioId)
        alcanciaViewModel.alcanciasPorUserLiveData.observe(viewLifecycleOwner) { alcancias ->
            if (!alcancias.isNullOrEmpty()) {
                alcanciaAdapter = AlcanciaAdapter(alcancias)
                alcanciaAdapter.setOnItemClickListener(this)
                binding.recyclerViewAlcancias.adapter = alcanciaAdapter
                binding.recyclerViewAlcancias.visibility = View.VISIBLE
                binding.ivListaVacia.visibility = View.GONE
                binding.tvMensajeVacio.visibility = View.GONE

                // Verificar si alguna alcancía alcanzó el 100%
                if (isFirstLoad && !isReload) {
                val alcanciaCompleta = alcancias.find { it.saldoActual >= it.meta}
                if (alcanciaCompleta != null) {
                    mostrarAnimacionYDialogo(alcanciaCompleta.nombre_alcancia)
                }
                    isFirstLoad = false
                }
            } else {
                binding.recyclerViewAlcancias.visibility = View.GONE
                binding.ivListaVacia.visibility = View.VISIBLE
                binding.tvMensajeVacio.visibility = View.VISIBLE
            }
        }
    }

    private fun mostrarAnimacionYDialogo(nombreAlcancia: String) {
        // Configurar LottieAnimationView
        val lottieAnimationView = LottieAnimationView(requireContext()).apply {
            setAnimation(R.raw.felicitaciones) // Archivo JSON de la animación
            repeatCount = 1 // Repetir una sola vez
            playAnimation() // Iniciar la animación
        }

        // Mostrar la animación en un diálogo personalizado
        val dialog = Dialog(requireContext()).apply {
            setContentView(lottieAnimationView)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Fondo transparente
            setCancelable(false) // Evitar que el diálogo se cierre al tocar fuera
        }

        dialog.show()

        // Cuando la animación termine, mostrar el AlertDialog
        lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                dialog.dismiss() // Cerrar el diálogo de la animación
                mostrarDialogoMetaCompletada(nombreAlcancia)
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun mostrarDialogoMetaCompletada(nombreAlcancia: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("¡Meta completada!")
            .setMessage("Has alcanzado tu meta de ahorro para la alcancía $nombreAlcancia.")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }


    override fun onItemClick5(alcancia: AlcanciaDTO) {
        // Crear un Intent para iniciar la Activity de depósitos
        val intent = Intent(requireContext(), Depositos::class.java).apply {
            Log.d("Alcancia", "ID de la alcancía seleccionada: ${alcancia.idAlcancia}, Usuario ID: $usuarioId")
            putExtra("idAlcancia", alcancia.idAlcancia) // Pasar el idAlcancia como extra
            putExtra("usuarioId", usuarioId) // Pasar el usuarioId como extra
        }
        startActivity(intent) // Iniciar la Activity
    }

    @SuppressLint("MissingInflatedId")
    override fun onItemClickModificar(alcancia: AlcanciaDTO) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_modificar_alcancia, null)

        val editTextNombreAlcancia = dialogView.findViewById<EditText>(R.id.editTextNombreAlcancia)
        val editTextMeta = dialogView.findViewById<EditText>(R.id.editTextMeta)
        val editTextSaldoActual = dialogView.findViewById<EditText>(R.id.editTextSaldoActual)
        val editTextCodigo = dialogView.findViewById<EditText>(R.id.editTextCodigo)
        val editTextFechaCreacion = dialogView.findViewById<EditText>(R.id.editTextFechaCreacion)
        val btnEliminar = dialogView.findViewById<Button>(R.id.btnEliminarAlcancia)

        editTextNombreAlcancia.setText(alcancia.nombre_alcancia)
        editTextMeta.setText(alcancia.meta.toString())
        editTextSaldoActual.setText(alcancia.saldoActual.toString())
        editTextCodigo.setText(alcancia.codigo)
        editTextFechaCreacion.setText(alcancia.fechaCreacion)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        btnEliminar.setOnClickListener {
            alcanciaViewModel.eliminarAlcancia(alcancia.idAlcancia)
            dialog.dismiss()
        }

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nombre = editTextNombreAlcancia.text.toString()
            val meta = editTextMeta.text.toString().toDoubleOrNull()
            val saldoActual = editTextSaldoActual.text.toString().toDoubleOrNull()
            val codigo = editTextCodigo.text.toString()
            val fechaCreacion = editTextFechaCreacion.text.toString()

            if (nombre.isBlank() || meta == null || saldoActual == null || codigo.isBlank() || fechaCreacion.isBlank()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Campos vacíos")
                    .setMessage("Por favor, complete todos los campos antes de guardar.")
                    .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                    .create()
                    .show()
                return@setOnClickListener
            }

            val alcanciaActualizada = alcancia.copy(
                nombre_alcancia = nombre,
                meta = meta,
                saldoActual = saldoActual,
                codigo = codigo,
                fechaCreacion = fechaCreacion
            )

            alcanciaViewModel.modificarAlcancia(alcancia.idAlcancia, alcanciaActualizada)
            dialog.dismiss()
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun registrar(usuarioId: Long) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nueva_alcancia, null)
        val editTextFechaCreacion = dialogView.findViewById<EditText>(R.id.editTextFechaCreacion)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Registrar Alcancía")
            .setView(dialogView)
            .setPositiveButton("Guardar", null) // Se asignará más tarde para no cerrar el diálogo automáticamente
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // Configurar el selector de fecha cuando el usuario haga clic en el campo de fecha
        editTextFechaCreacion.setOnClickListener {
            showDatePickerDialog(editTextFechaCreacion)
        }

        dialog.show()

        // Sobrescribir el comportamiento del botón "Guardar"
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nombreAlcancia = dialogView.findViewById<TextInputEditText>(R.id.editTextNombreAlcancia).text.toString()
            val meta = dialogView.findViewById<TextInputEditText>(R.id.editTextMeta).text.toString().toDoubleOrNull() ?: 0.0
            val saldoActual = dialogView.findViewById<TextInputEditText>(R.id.editTextSaldoActual).text.toString().toDoubleOrNull() ?: 0.0
            val codigo = dialogView.findViewById<TextInputEditText>(R.id.editTextCodigo).text.toString()
            val fechaCreacion = editTextFechaCreacion.text.toString()

            if (nombreAlcancia.isEmpty() || fechaCreacion.isEmpty()) {
                // Mostrar alerta sin cerrar el diálogo principal
                AlertDialog.Builder(requireContext())
                    .setTitle("Campos vacíos")
                    .setMessage("Por favor, complete todos los campos antes de guardar.")
                    .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                    .create()
                    .show()
                return@setOnClickListener
            }

            // Validar el formato de la fecha
            val parts = fechaCreacion.split("/")
            if (parts.size < 3) {
                // Mostrar alerta si el formato de la fecha es incorrecto
                AlertDialog.Builder(requireContext())
                    .setTitle("Formato de fecha incorrecto")
                    .setMessage("El formato de la fecha debe ser dd/MM/yyyy.")
                    .setPositiveButton("OK") { alertDialog, _ -> alertDialog.dismiss() }
                    .create()
                    .show()
                return@setOnClickListener
            }

            val dia = parts[0].padStart(2, '0')
            val mes = parts[1].padStart(2, '0')
            val anio = parts[2]
            val fechaoriginal = "${anio}-${mes}-${dia}"

            // Crear el objeto AlcanciaDTO
            val alcanciaDTO = AlcanciaDTO(
                idAlcancia = 0,
                nombre_alcancia = nombreAlcancia,
                meta = meta,
                saldoActual = saldoActual,
                codigo = codigo,
                fechaCreacion = fechaoriginal
            )

            // Registrar la alcancía
            alcanciaViewModel.registrarAlcancia(alcanciaDTO, usuarioId)
            Toast.makeText(requireContext(), "Alcancía registrada correctamente", Toast.LENGTH_SHORT).show()
            dialog.dismiss() // Cerrar el diálogo principal después del guardado exitoso
        }
    }



    private fun showDatePickerDialog(editTextFecha: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "${selectedDay.toString().padStart(2, '0')}/${(selectedMonth + 1).toString().padStart(2, '0')}/$selectedYear"
                editTextFecha.setText(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}