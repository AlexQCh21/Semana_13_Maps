package com.cursokotlin.s12_mvvm.repository

import android.util.Log
import com.cursokotlin.s12_mvvm.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirestoreManager {

    private val db = FirebaseFirestore.getInstance()

    companion object {
        const val TAG = "FirestoreManager"
        const val COLLECTION_USERS = "usuarios"
    }


    fun saveUser(user: Usuario, onResult: (Boolean, String?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid.isNullOrEmpty()) {
            onResult(false, "No hay UID disponible para guardar usuario.")
            return
        }

        user.id = uid // Asigna el UID
        db.collection(COLLECTION_USERS).document(uid)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "Usuario guardado con éxito en Firestore con ID: $uid")
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al guardar usuario en Firestore", e)
                onResult(false, e.message)
            }
    }


    suspend fun saveUserSuspend(usuario: Usuario): Boolean =
        suspendCancellableCoroutine { cont ->
            saveUser(usuario) { isSaved, _ ->
                cont.resume(isSaved)
            }
        }
}