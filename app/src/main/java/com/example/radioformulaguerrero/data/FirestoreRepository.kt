package com.example.radioformulaguerrero.data

import com.example.radioformulaguerrero.model.Programa
import com.example.radioformulaguerrero.model.Publicacion
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log

suspend fun obtenerPublicaciones(): List<Publicacion> {
    val db = FirebaseFirestore.getInstance()

    return try {
        val snapshot = db.collection("quejas")
            .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(20) // Limitar a 20 publicaciones para mejorar rendimiento
            .get()
            .await()
    
        val lista = snapshot.documents.mapNotNull { document ->
            document.toObject(Publicacion::class.java)
        }
        Log.d("FIREBASE", "Publicaciones recibidas: ${lista.size} elementos")
        lista
    } catch (e: Exception) {
        Log.e("FIREBASE_ERROR", "Error obteniendo publicaciones", e)
        emptyList()
    }
}

suspend fun obtenerProgramacionGenerica(): List<Map<String, Any?>> {
    val db = FirebaseFirestore.getInstance()
    return try {
        val snapshot = db.collection("programacion").get().await()
        snapshot.documents.map { doc ->
            val data = doc.data ?: emptyMap()
            val mutable = data.toMutableMap()
            mutable["id"] = doc.id
            mutable
        }
    } catch (e: Exception) {
        emptyList()
    }
}

fun agregarPublicacion(
    nuevaPublicacion: Publicacion,
    onSuccess: () -> Unit,
    onError: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("quejas")
        .add(nuevaPublicacion)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onError(e) }
}

fun actualizarPublicacion(
    docId: String,
    nuevaPublicacion: Publicacion,
    onSuccess: () -> Unit,
    onError: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("quejas").document(docId)
        .set(nuevaPublicacion)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onError(e) }
}

fun eliminarPublicacion(
    docId: String,
    onSuccess: () -> Unit,
    onError: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("quejas").document(docId)
        .delete()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onError(e) }
} 