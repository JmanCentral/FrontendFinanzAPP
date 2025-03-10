package com.practica.finazapp.Vista

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.practica.finazapp.Entidades.CalificacionDTO
import com.practica.finazapp.Entidades.ConsejosDTO
import com.practica.finazapp.ViewModelsApiRest.AlertViewModel
import com.practica.finazapp.ViewModelsApiRest.CalificacionViewModel
import com.practica.finazapp.ViewModelsApiRest.ConsejosViewModel
import com.practica.finazapp.ViewModelsApiRest.IncomeViewModel
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.databinding.FragmentPerfilBinding

class Perfil : Fragment() {

    private var usuarioId: Long = -1
    private lateinit var consejoViewModel: ConsejosViewModel
    private lateinit var calificacionViewModel: CalificacionViewModel
    private lateinit var consejosAdapter: ConsejosAdapter

    // Declaración de la variable de enlace
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el ViewModel compartido (asegúrate de tenerlo correctamente inicializado)
        val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        // Observar el usuarioId
        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { id ->
            Log.d("FragmentPerfil", "Usuario ID recibido: $id")
            usuarioId = id ?: -1
        }

        // Inicializar ViewModels
        consejoViewModel = ViewModelProvider(this)[ConsejosViewModel::class.java]
        calificacionViewModel = ViewModelProvider(this)[CalificacionViewModel::class.java]

        // Configurar RecyclerView
        binding.recyclerViewConsejos.layoutManager = LinearLayoutManager(requireContext())
        consejosAdapter = ConsejosAdapter(emptyList(), emptyMap(), ::onLikeClick, ::onDislikeClick)
        binding.recyclerViewConsejos.adapter = consejosAdapter

        // Observar los consejos
        consejoViewModel.obtenerConsejos()
        consejoViewModel.consejosResponse.observe(viewLifecycleOwner) { consejos ->
            if (!consejos.isNullOrEmpty()) {
                obtenerCalificaciones(consejos) // Llamar a calificaciones cuando ya tengamos los consejos
            } else {
                binding.recyclerViewConsejos.visibility = View.GONE
            }
        }

        // Observar las calificaciones
        calificacionViewModel.calificacionList.observe(viewLifecycleOwner) { calificaciones ->
            Log.d("FragmentPerfil", "Calificaciones recibidas: ${calificaciones?.size}")

            calificaciones?.forEach { calificacion ->
                Log.d("FragmentPerfil", "Calificación -> ConsejoID: ${calificacion.idConsejo}, Me Gusta: ${calificacion.me_gusta}, No Me Gusta: ${calificacion.no_me_gusta}")
            }
            val consejos = consejoViewModel.consejosResponse.value ?: return@observe
            actualizarLista(consejos, calificaciones)
        }
    }


    private fun obtenerCalificaciones(consejos: List<ConsejosDTO>) {
        consejos.forEach { consejo ->
            calificacionViewModel.obtenerCalificacion(consejo.id.toLong())
        }
    }

    private fun actualizarLista(consejos: List<ConsejosDTO>, calificaciones: List<CalificacionDTO>?) {
        val calificacionesMap = calificaciones?.associateBy { it.idConsejo ?: 0L } ?: emptyMap()
        consejosAdapter = ConsejosAdapter(consejos, calificacionesMap, ::onLikeClick, ::onDislikeClick)
        binding.recyclerViewConsejos.adapter = consejosAdapter
        binding.recyclerViewConsejos.visibility = View.VISIBLE
    }

    private fun onLikeClick(consejo: ConsejosDTO) {
        if (usuarioId == -1L) {
            Log.e("FragmentPerfil", "Error: usuarioId no válido")
            return
        }

        val nuevaCalificacion = CalificacionDTO(
            idCalificacion = null,
            me_gusta = 1, // Sumar un like
            no_me_gusta = 0,
            id_usuario = usuarioId, // Usar el ID real del usuario
            idConsejo = consejo.id.toLong()
        )
        calificacionViewModel.registrarCalificacion(nuevaCalificacion)
    }

    private fun onDislikeClick(consejo: ConsejosDTO) {
        if (usuarioId == -1L) {
            Log.e("FragmentPerfil", "Error: usuarioId no válido")
            return
        }

        val nuevaCalificacion = CalificacionDTO(
            idCalificacion = null,
            me_gusta = 0,
            no_me_gusta = 1, // Sumar un dislike
            id_usuario = usuarioId, // Usar el ID real del usuario
            idConsejo = consejo.id.toLong()
        )
        calificacionViewModel.registrarCalificacion(nuevaCalificacion)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
