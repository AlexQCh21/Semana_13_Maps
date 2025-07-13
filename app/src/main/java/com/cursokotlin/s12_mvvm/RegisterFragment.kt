package com.cursokotlin.s12_mvvm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cursokotlin.s12_mvvm.databinding.FragmentRegisterBinding
import com.cursokotlin.s12_mvvm.model.Usuario
import com.cursokotlin.s12_mvvm.repository.RepositoryUsuario
import com.cursokotlin.s12_mvvm.viewmodel.ValidRegisterViewModel
import com.cursokotlin.s12_mvvm.viewmodel.RepositoryViewModel
import com.cursokotlin.s12_mvvm.viewmodel.RepositoryViewModelFactory


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ValidRegisterViewModel by viewModels()
    private val viewModelRepositoryUsuario: RepositoryViewModel by viewModels {
        RepositoryViewModelFactory(RepositoryUsuario())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val breathAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.breath)
        binding.btnBread.startAnimation(breathAnim)
        observeRepositoryResult()
        observerDeleteCurrentUser()
        eventsComponents()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun eventsComponents(){
        //Boton para regresar al login
        binding.login.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        //Validación del nombre de usuario
        binding.editTextUsername.addTextChangedListener { username->
            val usernameStr = username?.toString()?:""
            viewModel.validateUsername(usernameStr)

            val error = viewModel.usernameError.value
            binding.editTextUsername.error = if(usernameStr.isNotEmpty()) error else null
        }

        //Validación de nombres
        binding.editTextNombres.addTextChangedListener { nombres->
            val nombresStr = nombres?.toString()?:""
            viewModel.validateNombre(nombresStr)

            val error = viewModel.nombresError.value
            binding.editTextNombres.error = if(nombresStr.isNotEmpty()) error else null
        }

        //Validación de apellidos
        binding.editTextApellidos.addTextChangedListener { apellidos->
            val apellidosStr = apellidos?.toString()?:""
            viewModel.validateApellido(apellidosStr)

            val error = viewModel.apellidosError.value
            binding.editTextApellidos.error = if(apellidosStr.isNotEmpty()) error else null
        }

        //Validación de telefono
        binding.editTextTelefono.addTextChangedListener { telefono ->
            val telefonoStr = telefono?.toString()?:""
            viewModel.validateTelefono(telefonoStr)

            val error = viewModel.telefonoError.value
            binding.editTextTelefono.error = if(telefonoStr.isNotEmpty()) error else null
        }



        //Validación del Email
        binding.editTextEmail.addTextChangedListener { email ->
            val emailStr = email?.toString() ?: ""
            viewModel.validateEmail(emailStr)

            val error = viewModel.emailError.value
            binding.editTextEmail.error = if (emailStr.isNotEmpty()) error else null
        }

        //Validación de la contraseña
        binding.editTextPassword.addTextChangedListener { clave ->
            val claveStr = clave?.toString() ?: ""
            viewModel.validateClave(claveStr)

            val errorClave = viewModel.claveError.value
            binding.editTextPassword.error = if (claveStr.isNotEmpty()) errorClave else null
        }

        //Validación de la confirmacion de contraseña
        binding.editTextConfimPassword.addTextChangedListener { claveConfirmacion ->
            val claveConfirmacionStr = claveConfirmacion?.toString() ?: ""
            viewModel.validateClaveConfirmacion(claveConfirmacionStr)

            val error = viewModel.claveConfirmacionError.value
            binding.editTextConfimPassword.error = if (claveConfirmacionStr.isNotEmpty()) error else null
        }

        binding.btnBread.setOnClickListener {
            // Forzar validación de todos los campos antes de verificar
            viewModel.validateEmail(viewModel.email.value ?: "")
            viewModel.validateUsername(viewModel.username.value ?: "")
            viewModel.validateNombre(viewModel.nombres.value ?: "")
            viewModel.validateApellido(viewModel.apellidos.value ?: "")
            viewModel.validateTelefono(viewModel.telefono.value ?: "")
            viewModel.validateClave(viewModel.clave.value ?: "")
            viewModel.validateClaveConfirmacion(viewModel.claveConfirmacion.value ?: "")

            if(viewModel.isValidAll()){
                //registramos al usuario
                val usuario = Usuario(
                    nombreUsuario = viewModel.username.value ?: "",
                    nombre = viewModel.nombres.value ?: "",
                    apellido = viewModel.apellidos.value ?: "",
                    email = viewModel.email.value ?: "",
                    telefono = viewModel.telefono.value ?: ""
                )
                Toast.makeText(requireContext(),usuario.email, Toast.LENGTH_SHORT).show()
                viewModelRepositoryUsuario.registrar(
                    usuario,
                    viewModel.clave.value?:""
                )

            }else{
                Toast.makeText(requireContext(), "Corrige los errores", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeRepositoryResult() {
        viewModelRepositoryUsuario.registroResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
            }else{
                viewModelRepositoryUsuario.deleteCurrentUser()
            }
        }
    }

    private fun observerDeleteCurrentUser(){
        viewModelRepositoryUsuario.deleteResult.observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted == true) {
                Toast.makeText(requireContext(), "Usuario eliminado por error en registro", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error al eliminar el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }




}