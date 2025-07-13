package com.cursokotlin.s12_mvvm.model

import com.google.firebase.database.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Usuario(
    var id: String? = null,

    @JvmField
    @PropertyName("nombre_usuario")
    var nombreUsuario: String = "",

    @JvmField
    @PropertyName("nombre")
    var nombre: String = "",

    @JvmField
    @PropertyName("apellido")
    var apellido: String = "",

    @JvmField
    @PropertyName("email")
    var email: String = "",

    @JvmField
    @PropertyName("telefono")
    var telefono: String = "",

    @ServerTimestamp
    @JvmField
    @PropertyName("create_at")
    var createAt: Date? = null,
) {
    constructor() : this(
        id = null,
        nombreUsuario = "",
        nombre = "",
        apellido = "",
        email = "",
        telefono = "",
        createAt = null
    )
}
