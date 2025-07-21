package com.cursokotlin.cuchareable.repository

import android.util.Log
import com.cursokotlin.cuchareable.model.Point
import com.cursokotlin.cuchareable.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class FirestoreManager {

    private val db = FirebaseFirestore.getInstance()

    companion object {
        const val TAG = "FirestoreManager"
        const val COLLECTION_USERS = "usuarios"
        const val COLLECTION_POINTS = "points"
        const val SUBCOLLECTION_FAVORITOS = "favoritos"
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

    // Guarda un nuevo punto en Firestore
    suspend fun addPoint(point: Point): Boolean {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid.isNullOrEmpty()) {
                Log.e(TAG, "Usuario no autenticado, no se puede guardar el punto")
                return false
            }

            point.id = db.collection(COLLECTION_POINTS).document().id
            db.collection(COLLECTION_POINTS)
                .document(point.id!!)
                .set(point)
                .await()

            Log.d(TAG, "Punto guardado correctamente con ID: ${point.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar el punto", e)
            false
        }
    }

    // Obtiene los puntos del usuario actual
    suspend fun getMyPoints(): List<Point> {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid.isNullOrEmpty()) {
                Log.e(TAG, "Usuario no autenticado, no se pueden traer puntos")
                return emptyList()
            }

            val snapshot = db.collection(COLLECTION_POINTS)
                .whereEqualTo("nombreUsuario", uid) // Filtra por usuario
                .get()
                .await()

            snapshot.toObjects(Point::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener puntos del usuario", e)
            emptyList()
        }
    }

    // Obtiene todos los puntos de Firestore
    suspend fun getAllPoints(): List<Point> {
        return try {
            val snapshot = db.collection(COLLECTION_POINTS)
                .get()
                .await()

            snapshot.toObjects(Point::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener todos los puntos", e)
            emptyList()
        }
    }


    suspend fun saveUserSuspend(usuario: Usuario): Boolean =
        suspendCancellableCoroutine { cont ->
            saveUser(usuario) { isSaved, _ ->
                cont.resume(isSaved)
            }
        }


    // Agregar un favorito
    suspend fun addFavorite(pointId: String): Boolean {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return false
            val favorito = mapOf(
                "pointId" to pointId,
                "fechaAgregado" to FieldValue.serverTimestamp()
            )
            db.collection(COLLECTION_USERS)
                .document(uid)
                .collection(SUBCOLLECTION_FAVORITOS)
                .document(pointId)
                .set(favorito)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Error al agregar favorito", e)
            false
        }
    }

    // Eliminar un favorito
    suspend fun removeFavorite(pointId: String): Boolean {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return false
            db.collection(COLLECTION_USERS)
                .document(uid)
                .collection(SUBCOLLECTION_FAVORITOS)
                .document(pointId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Error al eliminar favorito", e)
            false
        }
    }

    // Verificar si un point es favorito
    suspend fun isFavorite(pointId: String): Boolean {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return false
            val snapshot = db.collection(COLLECTION_USERS)
                .document(uid)
                .collection(SUBCOLLECTION_FAVORITOS)
                .document(pointId)
                .get()
                .await()
            snapshot.exists()
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Error al verificar favorito", e)
            false
        }
    }

    // Traer todos los favoritos
    suspend fun getFavorites(): List<String> {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyList()
            val snapshot = db.collection(COLLECTION_USERS)
                .document(uid)
                .collection(SUBCOLLECTION_FAVORITOS)
                .get()
                .await()
            snapshot.documents.map { it.id }
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Error al obtener favoritos", e)
            emptyList()
        }
    }
}