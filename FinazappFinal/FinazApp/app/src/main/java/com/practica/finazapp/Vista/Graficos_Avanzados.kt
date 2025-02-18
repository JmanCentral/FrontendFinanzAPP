package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
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
import com.example.finanzapp.viewmodels.AlcanciaViewModel
import com.google.android.material.textfield.TextInputEditText
import com.practica.finazapp.Entidades.AlcanciaDTO
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.databinding.FragmentGraficosAvanzadosBinding
import java.util.Calendar


class Graficos_Avanzados : Fragment(), AlcanciaListener {

    private var usuarioId: Long = -1
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

        binding.BuscarAlcancia.setOnClickListener {
            BuscarPorCodigo()
        }

        alcanciaViewModel.operacionCompletadaLiveData.observe(viewLifecycleOwner) { completada ->
            if (completada == true) {
                obtenerAlcancias(usuarioId)  // Usa usuarioId directamente
            }
        }
    }

    private fun BuscarPorCodigo() {
        // Inflar la vista del diálogo
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_buscar_alcancia, null)
        val editTextNombre = dialogView.findViewById<EditText>(R.id.Busca_alcancia)
        val buttonalcancia = dialogView.findViewById<Button>(R.id.buttonalcancia)

        // Crear el diálogo
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Buscar Alcancía")
            .setView(dialogView)
            .create()

        // Configurar el listener del botón
        buttonalcancia.setOnClickListener {
            val codigo = editTextNombre.text.toString().trim()
            if (codigo.isNotBlank()) {
                // Redirigir a la nueva Activity
                val intent = Intent(requireContext(), Alcancias_Compartidas::class.java).apply {
                    putExtra("codigo", codigo)
                    putExtra("usuarioId", usuarioId)
                }
                startActivity(intent)
            } else {
                // Mostrar mensaje de error si el campo está vacío
                Toast.makeText(requireContext(), "Ingrese un código válido", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        // Mostrar el diálogo
        dialog.show()
    }

    private fun obtenerAlcancias(usuarioId: Long) {
        alcanciaViewModel.obtenerAlcanciasPorUser(usuarioId)
        alcanciaViewModel.alcanciasPorUserLiveData.observe(viewLifecycleOwner) { alcancias ->
            if (!alcancias.isNullOrEmpty()) {
                alcanciaAdapter = AlcanciaAdapter(alcancias)
                alcanciaAdapter.setOnItemClickListener(this)
                binding.recyclerViewAlcancias.adapter = alcanciaAdapter
                binding.recyclerViewAlcancias.visibility = View.VISIBLE
                binding.ivListaVacia.visibility = View.GONE
            } else {
                binding.recyclerViewAlcancias.visibility = View.GONE
                binding.ivListaVacia.visibility = View.VISIBLE
            }
        }
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
    private fun registrar(usuarioId: Long) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nueva_alcancia, null)
        val editTextFechaCreacion = dialogView.findViewById<EditText>(R.id.editTextFechaCreacion)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Registrar Alcancía")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombreAlcancia = dialogView.findViewById<TextInputEditText>(R.id.editTextNombreAlcancia).text.toString()
                val meta = dialogView.findViewById<TextInputEditText>(R.id.editTextMeta).text.toString().toDoubleOrNull() ?: 0.0
                val saldoActual = dialogView.findViewById<TextInputEditText>(R.id.editTextSaldoActual).text.toString().toDoubleOrNull() ?: 0.0
                val codigo = dialogView.findViewById<TextInputEditText>(R.id.editTextCodigo).text.toString()
                val fechaCreacion = editTextFechaCreacion.text.toString()



                if (nombreAlcancia.isNotEmpty() && codigo.isNotEmpty() && fechaCreacion.isNotEmpty()) {

                    val parts = fechaCreacion.split("/")
                    val dia = parts[0].padStart(2, '0')
                    val mes = parts[1].padStart(2, '0')
                    val anio = parts[2]
                    val fechaoriginal = "${anio}-${mes}-${dia}"

                    val alcanciaDTO = AlcanciaDTO(
                        idAlcancia = 0,
                        nombre_alcancia = nombreAlcancia,
                        meta = meta,
                        saldoActual = saldoActual,
                        codigo = codigo,
                        fechaCreacion = fechaoriginal
                    )

                    alcanciaViewModel.registrarAlcancia(alcanciaDTO, usuarioId)
                    Toast.makeText(requireContext(), "Alcancía registrada correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // Configurar el selector de fecha cuando el usuario haga clic en el campo de fecha
        editTextFechaCreacion.setOnClickListener {
            showDatePickerDialog(editTextFechaCreacion)
        }

        dialog.show()
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