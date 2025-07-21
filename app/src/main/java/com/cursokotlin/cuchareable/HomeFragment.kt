package com.cursokotlin.cuchareable

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.cursokotlin.cuchareable.databinding.FragmentHomeBinding
import com.cursokotlin.cuchareable.recycler.PointsAdapter


class HomeFragment : Fragment() {
    lateinit var authManager: AuthManager
    lateinit var binding: FragmentHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Por defecto muestra "Todos"
        replaceFragment(AllFragment.newInstance(PointsAdapter.SectionType.ALL_POINTS))

        // Cambiar el fragmento según el botón seleccionado
        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val sectionType = when (checkedId) {
                    R.id.button1 -> PointsAdapter.SectionType.ALL_POINTS
                    R.id.button2 -> PointsAdapter.SectionType.FAVORITE_POINTS
                    R.id.button3 -> PointsAdapter.SectionType.MY_ADDED_POINTS
                    else -> PointsAdapter.SectionType.ALL_POINTS
                }

                val fragment = AllFragment.newInstance(sectionType)

                childFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
            }
        }

        // Configurar botón de cerrar sesión
        binding.btnLogout.setOnClickListener {
            cerrarSesion()
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authManager = AuthManager()
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        if(authManager.getCurrentUser()==null){
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        // Escucha el click en el botón flotante
        binding.btnAddMap.setOnClickListener {
            abrirFormularioDialog()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun cerrarSesion() {
        authManager.logout()
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }


    // Abre el formulario
    private fun abrirFormularioDialog() {
        val dialog = FormularioDialogFragment()
        dialog.show(parentFragmentManager, "FormularioDialog")
    }
}