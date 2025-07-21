package com.cursokotlin.cuchareable

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cursokotlin.cuchareable.databinding.FragmentLoginBinding
import com.cursokotlin.cuchareable.repository.ServicioInicioSesion
import com.cursokotlin.cuchareable.viewmodel.LoginViewModel
import com.cursokotlin.cuchareable.viewmodel.LoginViewModelFactory
import com.cursokotlin.cuchareable.viewmodel.ValidLoginViewModel


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ValidLoginViewModel by viewModels()
    private val viewModelLogin: LoginViewModel by viewModels {
        LoginViewModelFactory(ServicioInicioSesion())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        val breathAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.breath)
        binding.btnBread.startAnimation(breathAnim)

        setupObservers()
        setupListeners()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

        }

        return binding.root
    }

    private fun setupObservers() {
        viewModelLogin.loginResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }

        viewModelLogin.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModelLogin.errorMessage.value = null
            }
        }
    }

    private fun setupListeners() {
        // Botón para ir a registro
        binding.registrarme.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Validaciones en tiempo real
        binding.editTextEmail.addTextChangedListener { email ->
            val emailStr = email?.toString() ?: ""
            viewModel.validateEmail(emailStr)
            binding.editTextEmail.error = if (emailStr.isNotEmpty()) viewModel.emailError.value else null
        }

        binding.editTextPassword.addTextChangedListener { clave ->
            val claveStr = clave?.toString() ?: ""
            viewModel.validateClave(claveStr)
            binding.editTextPassword.error = if (claveStr.isNotEmpty()) viewModel.claveError.value else null
        }

        // Botón de login
        binding.btnBread.setOnClickListener {
            if (viewModel.emailError.value == null && viewModel.claveError.value == null) {
                viewModelLogin.login(
                    viewModel.email.value ?: "",
                    viewModel.clave.value ?: ""
                )
            } else {
                Toast.makeText(requireContext(), "Corrige los errores", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
