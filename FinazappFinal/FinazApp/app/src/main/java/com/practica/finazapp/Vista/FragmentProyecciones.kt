package com.practica.finazapp.Vista

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.practica.finazapp.R
import com.practica.finazapp.ViewModelsApiRest.AlertViewModel
import com.practica.finazapp.ViewModelsApiRest.IncomeViewModel
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.ViewModelsApiRest.SpendViewModel
import com.practica.finazapp.databinding.FragmentProyeccionesBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FragmentProyecciones : Fragment() {

    private var usuarioId: Long = -1
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var ingresosViewModel: IncomeViewModel
    private lateinit var gastosViewModel: SpendViewModel
    private lateinit var alertaViewModel: AlertViewModel

    private var _binding: FragmentProyeccionesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        _binding = FragmentProyeccionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        ingresosViewModel = ViewModelProvider(this)[IncomeViewModel::class.java]
        gastosViewModel = ViewModelProvider(this)[SpendViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { usuarioId ->
            this.usuarioId = usuarioId

            gastosViewModel.obtenerValorGastosMes(usuarioId)
            gastosViewModel.valorGastosMesLiveData.observe(viewLifecycleOwner) { gastosMes ->

                if (gastosMes != null) {
                    MostrarRecomendaciones()
                } else {
                    // Mostrar un AlertDialog indicando que no hay gastos registrados
                    AlertDialog.Builder(requireContext())
                        .setTitle("Sin gastos registrados")
                        .setMessage("No tienes gastos registrados. Debes ingresar al menos un gasto antes de continuar.")
                        .setPositiveButton("Aceptar") { _, _ ->
                            // Navegar a otro Fragment (Ejemplo: DashboardFragment)
                            findNavController().navigate(R.id.Dashboard)
                        }
                        .setCancelable(false) // Evita que el usuario cierre el diálogo con el botón atrás
                        .show()

                }
            }
        }


        val botonfrecuente = view.findViewById<CardView>(R.id.cardView7)

        botonfrecuente.setOnClickListener {
            MostrarRecurrentes()
        }
    }

    private fun MostrarRecomendaciones() {


            gastosViewModel.listarGastosAlto(usuarioId)
            gastosViewModel.gastosAltosLiveData.observe(viewLifecycleOwner) { gastoMasAlto ->

                gastoMasAlto?.let {
                    // Asegúrate de tener el TextView del CardView que mostrará el gasto más alto
                    val textViewGastoMasAlto =
                        view?.findViewById<TextView>(R.id.textViewGastoMasAlto)

                    // Crea el mensaje con los datos del gasto
                    val mensaje =
                        "El gasto más alto es '${it.nombre_gasto}' de la categoría '${it.categoria}' con un valor de \$${it.valor}. Te recomendamos reducir ese gasto significativamente."

                    // Actualiza el TextView con el mensaje
                    textViewGastoMasAlto?.text = mensaje
                }
            }
            gastosViewModel.listarGastosBajo(usuarioId)
            gastosViewModel.gastosBajosLiveData.observe(viewLifecycleOwner) { gastoMasBajo ->
                gastoMasBajo?.let {

                    val textViewGastoMasBajo =
                        view?.findViewById<TextView>(R.id.textViewGastoMasBajo)
                    val mensaje =
                        "El gasto más bajo es '${it.nombre_gasto}' de la categoría '${it.categoria}' con un valor de \$${it.valor}. Te recomendamos que sigas así de juicioso."
                    textViewGastoMasBajo?.text = mensaje

                }

            }

            gastosViewModel.obtenerGastoRecurrente(usuarioId)
            gastosViewModel.gastoRecurrenteLiveData.observe(viewLifecycleOwner) { descripcionRecurrente ->
                descripcionRecurrente?.let {
                    Log.d("descripcionRecurrente", "recurrente: $descripcionRecurrente")
                    // Encuentra el TextView para mostrar el resultado
                    val textViewGastosRecurrentes = view?.findViewById<TextView>(R.id.textpromedio)
                    val mensaje =
                        "Tienes un gasto recurrente en : $descripcionRecurrente revisa si es esencial ese gasto"

                    textViewGastosRecurrentes?.text = mensaje
                }
            }
            gastosViewModel.obtenerPorcentajeGastos(usuarioId)
            gastosViewModel.porcentajeGastosLiveData
                .observe(viewLifecycleOwner) { porcentaje ->
                    porcentaje?.let {
                        Log.d("pruebis", "recurrente: $porcentaje")
                        val textViewGastoPorcentaje =
                            view?.findViewById<TextView>(R.id.textRecurrente)

                        // Calcular el mensaje con el porcentaje y el comentario

                        val mensaje = when {
                            it < 30 -> "Tus gastos están por debajo del 30% de tus ingresos: ${
                                String.format(
                                    "%.2f%%",
                                    it
                                )
                            }. ¡Excelente gestión!"

                            it in 30.0..50.0 -> "Tus gastos representan entre el 30% y el 50% de tus ingresos: ${
                                String.format(
                                    "%.2f%%",
                                    it
                                )
                            }. Considera revisar tus gastos."

                            else -> "Tus gastos superan el 50% de tus ingresos: ${
                                String.format(
                                    "%.2f%%",
                                    it
                                )
                            }. Te recomendamos ajustar tu presupuesto."
                        }
                        // Actualizar el TextView con el mensaje completo
                        textViewGastoPorcentaje?.text = mensaje
                    }
                }
            ingresosViewModel.obtenerProyeccionIngresos(usuarioId)
            ingresosViewModel.proyeccionIngresosLiveData.observe(viewLifecycleOwner) { proyectado ->
                proyectado?.let {

                    val textViewProyectado = view?.findViewById<TextView>(R.id.textProyeciones)
                    val mensaje =
                        "Ingresos proyectados para el siguiente mes : $${String.format("%.2f", it)}"
                    textViewProyectado?.text = mensaje

                }
            }
            gastosViewModel.CategoriaMasAlta.observe(viewLifecycleOwner) { listaCategorias ->
                listaCategorias?.let {
                    if (!listaCategorias.isNullOrEmpty()) {
                        val categoriaMasAlta =
                            listaCategorias[0] // Asumiendo que la lista está ordenada de mayor a menor
                        val textViewRecomendacion =
                            view?.findViewById<TextView>(R.id.textRecomendacion)
                        val mensaje =
                            "Revisa tus gastos en '${categoriaMasAlta.categoria}', ya que suman ${categoriaMasAlta.totalValor} y es la categoría que más gastas"
                        textViewRecomendacion?.text = mensaje
                    }
                }
            }
            gastosViewModel.obtenerPromedioDiario(usuarioId)
            gastosViewModel.promedioDiarioLiveData.observe(viewLifecycleOwner) { gastosPromedioDiario ->

                gastosPromedioDiario?.let {
                    val textViewPromedioDiario =
                        view?.findViewById<TextView>(R.id.textRecomendacion1)
                    val mensaje =
                        "El promedio diario de tus gastos es de  : $${String.format("%.2f", it)}"
                    textViewPromedioDiario?.text = mensaje

                }
            }


    }

    private fun MostrarRecurrentes() {

        val intent = Intent(requireContext(), Recurrente::class.java).apply {
            putExtra("usuarioId", usuarioId)
        }
        startActivity(intent)
    }

}