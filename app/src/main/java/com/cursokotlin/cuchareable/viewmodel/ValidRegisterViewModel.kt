package com.cursokotlin.cuchareable.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ValidRegisterViewModel:ViewModel() {

    var email = MutableLiveData<String>()
    var emailError = MutableLiveData<String>()

    var username = MutableLiveData<String>()
    var usernameError = MutableLiveData<String>()

    var nombres = MutableLiveData<String>()
    var nombresError = MutableLiveData<String>()

    var apellidos = MutableLiveData<String>()
    var apellidosError = MutableLiveData<String>()

    var telefono = MutableLiveData<String>()
    var telefonoError = MutableLiveData<String>()

    var clave = MutableLiveData<String>()
    var claveError = MutableLiveData<String?>()

    var claveConfirmacion = MutableLiveData<String>()
    var claveConfirmacionError = MutableLiveData<String>()

    fun validateEmail(newEmail:String){
        email.value=newEmail
        emailError.value = if (isValidEmail(newEmail)) null else "Correo inválido"
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validateClave(newClave:String){
        clave.value=newClave
        claveError.value = isValidPassword(newClave)
    }

    private fun isValidPassword(clave: String): String? {
        // Al menos 1 mayúscula
        val hasUppercase = clave.any { it.isUpperCase() }
        if (!hasUppercase){
            return "Debe de tener al menos una mayúscula"
        }

        // Al menos 1 número
        val hasDigit = clave.any { it.isDigit() }
        if (!hasDigit){
            return "Debe de tener al menos un número"
        }

        // Al menos 1 carácter especial (#, @, *, etc.)
        val hasSpecialChar = clave.any { "!@#\$%^&*()_+-=[]{}|;':,.<>/?".contains(it) }
        if (!hasSpecialChar){
            return "Debe de tener al menos un caracter especial"
        }

        // Longitud mínima de 8
        val hasMinLength = clave.length >= 8
        if (!hasMinLength){
            return "Debe de ser mínimo de 8 caracteres"
        }

        return null
    }

    fun validateClaveConfirmacion(newClave: String) {
        claveConfirmacion.value = newClave

        claveConfirmacionError.value = when {
            clave.value.isNullOrEmpty() -> "Primero ingresa la contraseña principal"
            clave.value != newClave -> "Las claves no coinciden"
            else -> null
        }
    }


    fun validateUsername(newUsername: String) {
        username.value = newUsername
        usernameError.value = if (newUsername.isBlank()) "No debe estar vacío" else null
    }

    fun validateNombre(newNombre: String) {
        nombres.value = newNombre
        nombresError.value = if (newNombre.isBlank()) "No debe estar vacío" else null
    }

    fun validateApellido(newApellido: String) {
        apellidos.value = newApellido
        apellidosError.value = if (newApellido.isBlank()) "No debe estar vacío" else null
    }

    fun validateTelefono(newTelefono:String){
        telefono.value = newTelefono
        telefonoError.value = if (newTelefono.length != 9 || !newTelefono.all { it.isDigit() }) {
            "Debe tener exactamente 9 números"
        } else null

    }

    fun isValidAll(): Boolean {
        return listOf(
            usernameError.value,
            claveError.value,
            nombresError.value,
            apellidosError.value,
            claveConfirmacionError.value,
            emailError.value
        ).all { it == null }
    }


}