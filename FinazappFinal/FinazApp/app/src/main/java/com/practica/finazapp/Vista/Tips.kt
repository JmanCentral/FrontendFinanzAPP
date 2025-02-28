package com.practica.finazapp.Vista

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.practica.finazapp.Entidades.TipsDTO
import com.practica.finazapp.ViewModelsApiRest.SharedViewModel
import com.practica.finazapp.ViewModelsApiRest.TipsViewModel
import com.practica.finazapp.databinding.FragmentTipsBinding


class Tips : Fragment() , OnTipClickListener {

    private var usuarioId: Long = -1
    private var _binding: FragmentTipsBinding? = null
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var tipsViewModel: TipsViewModel
    private lateinit var tipsAdapter: TipsAdapter
    private var isRecyclerViewEnabled = true

    // Getter seguro para evitar accesos nulos
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTipsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        tipsViewModel = ViewModelProvider(this)[TipsViewModel::class.java]

        binding.recyclerViewTips.layoutManager = LinearLayoutManager(requireContext())
        tipsAdapter = TipsAdapter(emptyList(), this) // Inicializar con lista vacía
        binding.recyclerViewTips.adapter = tipsAdapter

        // Deshabilitar el scroll si la bandera está en false
        binding.recyclerViewTips.setOnTouchListener { _, _ -> !isRecyclerViewEnabled }

        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { id ->
            Log.d("Alcancia", "Usuario ID observado: $id")
            usuarioId = id
            obtenerTips(id)
        }
    }

    override fun onTipClicked(tip: TipsDTO) {
        Toast.makeText(requireContext(), "Tip desbloqueado: ${tip.titulo}", Toast.LENGTH_SHORT).show()
    }

    private fun obtenerTips(usuarioId: Long) {

        tipsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.lottieLoading.visibility = View.VISIBLE
                binding.layoutTips.visibility = View.GONE
            } else {
                binding.lottieLoading.visibility = View.GONE
                binding.layoutTips.visibility = View.VISIBLE
            }
        }

        tipsViewModel.obtenerTips(usuarioId)
        tipsViewModel.obtenertips.observe(viewLifecycleOwner) { tips ->
            if (!tips.isNullOrEmpty()) {
                tipsAdapter = TipsAdapter(tips, this)
                binding.recyclerViewTips.adapter = tipsAdapter
                binding.recyclerViewTips.visibility = View.VISIBLE
            } else {
                binding.recyclerViewTips.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita memory leaks
    }
}
