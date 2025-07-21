package com.cursokotlin.cuchareable.model

import com.google.firebase.database.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Point(
    var id: String? = null,

    @JvmField
    @PropertyName("referencia")
    var referencia: String = "",

    @JvmField
    @PropertyName("nombre")
    var nombre: String = "",

    @JvmField
    @PropertyName("tipo")
    var tipo: String = "",

    @JvmField
    @PropertyName("longitud")
    var longitud: Double = 0.0,

    @JvmField
    @PropertyName("latitud")
    var latitud: Double = 0.0,

    @JvmField
    @PropertyName("imagen_url")
    var imagenUrl: String = "",

    @JvmField
    @PropertyName("user_id")
    var userId: String = "",

    @ServerTimestamp
    @JvmField
    @PropertyName("create_at")
    var createAt: Date? = null,
) {
    constructor() : this(
        id = null,
        nombre = "",
        tipo = "",
        longitud = 0.0,
        latitud = 0.0,
        imagenUrl = "",
        userId = "",
        createAt = null
    )
}
