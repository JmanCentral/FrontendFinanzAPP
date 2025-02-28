package com.practica.finazapp.Vista

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.practica.finazapp.ViewModelsApiRest.AlertViewModel
import com.practica.finazapp.ViewModelsApiRest.ConsejosViewModel
import com.practica.finazapp.ViewModelsApiRest.IncomeViewModel
import com.practica.finazapp.databinding.FragmentPerfilBinding

class Perfil : Fragment() {

    private lateinit var consejoViewModel: ConsejosViewModel
    private lateinit var consejosAdapter: ConsejosAdapter

    // Declaración de la variable de enlace
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar el diseño del fragmento utilizando el enlace de datos generado
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar el ViewModel
        consejoViewModel = ViewModelProvider(this)[ConsejosViewModel::class.java]
        consejoViewModel.obtenerConsejos()
        // Configurar RecyclerView
        binding.recyclerViewConsejos.layoutManager = LinearLayoutManager(requireContext())
        consejosAdapter = ConsejosAdapter(emptyList()) // Inicializar con lista vacía
        binding.recyclerViewConsejos.adapter = consejosAdapter


        consejoViewModel.consejosResponse.observe(viewLifecycleOwner) { consejos ->
            Log.d("FragmentPerfil", "Consejos recibidos: $consejos")
            if (!consejos.isNullOrEmpty()) {
                consejosAdapter = ConsejosAdapter(consejos)
                binding.recyclerViewConsejos.visibility = View.VISIBLE
                binding.lottieLoading.visibility = View.GONE
                binding.recyclerViewConsejos.adapter = consejosAdapter
            } else {
                binding.recyclerViewConsejos.visibility = View.GONE
                binding.lottieLoading.visibility = View.VISIBLE
            }
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
