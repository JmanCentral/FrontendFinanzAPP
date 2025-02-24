package com.practica.finazapp.Vista

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.practica.finazapp.ViewModelsApiRest.AlertViewModel
import com.practica.finazapp.ViewModelsApiRest.ConsejosViewModel
import com.practica.finazapp.ViewModelsApiRest.IncomeViewModel
import com.practica.finazapp.databinding.FragmentPerfilBinding

class Perfil : Fragment() {

    private lateinit var consejoViewModel: ConsejosViewModel

    // Declaración de la variable de enlace
    private var _binding: FragmentPerfilBinding? = null

    // Esta propiedad es válida solo entre onCreateView y onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar el diseño del fragmento utilizando el enlace de datos generado
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Devolver la vista raíz del diseño inflado
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        consejoViewModel = ViewModelProvider(this)[ConsejosViewModel::class.java]

        MostrarConsejos()

    }

    private fun MostrarConsejos() {

        consejoViewModel.obtenerConsejos()
        consejoViewModel.consejosResponse.observe(viewLifecycleOwner) { consejos ->
            binding.textViewConsejo1.text = consejos!!.consejo1
            binding.textViewConsejo2.text = consejos!!.consejo2
            binding.textViewConsejo3.text = consejos!!.consejo3
            binding.textViewConsejo4.text = consejos!!.consejo4
            binding.textViewConsejo5.text = consejos!!.consejo5
            binding.textViewConsejo6.text = consejos!!.consejo6
            binding.textViewConsejo7.text = consejos!!.consejo7
            binding.textViewConsejo8.text = consejos!!.consejo8
            binding.textViewConsejo9.text = consejos!!.consejo9
        }
    }

}
