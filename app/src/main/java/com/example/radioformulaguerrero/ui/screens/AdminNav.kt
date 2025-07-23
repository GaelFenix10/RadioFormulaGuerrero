package com.example.radioformulaguerrero.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import android.util.Log
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.radioformulaguerrero.model.Publicacion
import com.example.radioformulaguerrero.data.obtenerPublicaciones
import com.example.radioformulaguerrero.data.actualizarPublicacion
import com.example.radioformulaguerrero.data.eliminarPublicacion
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AdminNav() {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf<String?>(null) }
    var imagenUrl by remember { mutableStateOf("") }
    var newsurl by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    var expanded by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    var publicaciones by remember { mutableStateOf<List<Pair<String, Publicacion>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var publicacionAEditar by remember { mutableStateOf<Pair<String, Publicacion>?>(null) }

    // Cargar publicaciones
    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        try {
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val snapshot = db.collection("quejas").orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING).get().await()
            publicaciones = snapshot.documents.mapNotNull { doc ->
                val pub = doc.toObject(Publicacion::class.java)
                if (pub != null) doc.id to pub else null
            }
        } catch (e: Exception) {
            error = "Error al cargar publicaciones: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)) {
        Text("Panel de administración (AdminNav)", style = MaterialTheme.typography.titleMedium)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
                .animateContentSize()
                .clickable { expanded = !expanded },
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            if (expanded) {
                Column(Modifier.padding(16.dp)) {
                    Text("Crear nueva publicación", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        minLines = 3,
                        maxLines = 10
                    )
                    OutlinedTextField(
                        value = imagenUrl,
                        onValueChange = { imagenUrl = it },
                        label = { Text("URL de imagen (opcional)") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    OutlinedTextField(
                        value = newsurl,
                        onValueChange = { newsurl = it },
                        label = { Text("URL de noticia externa (opcional)") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    if (saveError != null) {
                        Text(saveError!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                    }
                    Button(
                        onClick = {
                            isSaving = true
                            saveError = null
                            val db = FirebaseFirestore.getInstance()
                            val nueva = com.example.radioformulaguerrero.model.Publicacion(
                                titulo = titulo,
                                descripcion = descripcion,
                                fecha = com.google.firebase.Timestamp.now(),
                                imagenUrl = imagenUrl,
                                newsurl = newsurl
                            )
                            db.collection("quejas").add(nueva)
                                .addOnSuccessListener {
                                    isSaving = false
                                    titulo = ""
                                    descripcion = ""
                                    imagenUrl = ""
                                    newsurl = ""
                                    // Recargar publicaciones
                                    scope.launch {
                                        isLoading = true
                                        val snapshot = db.collection("quejas").orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING).get().await()
                                        publicaciones = snapshot.documents.mapNotNull { doc ->
                                            val pub = doc.toObject(Publicacion::class.java)
                                            if (pub != null) doc.id to pub else null
                                        }
                                        isLoading = false
                                    }
                                }
                                .addOnFailureListener { e ->
                                    isSaving = false
                                    saveError = "Error al guardar: ${e.message}"
                                    Log.e("PUBLICACION", "Error al guardar", e)
                                }
                        },
                        enabled = titulo.isNotBlank() && descripcion.isNotBlank() && !isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text("Guardar publicación")
                        }
                    }
                }
            } else {
                Box(Modifier.padding(16.dp)) {
                    Text("Crear nueva publicación (expandir para ver el formulario)", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Divider()
        // Mostrar publicaciones existentes
        Text("Publicaciones existentes", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        } else if (publicaciones.isEmpty()) {
            Text("No hay publicaciones")
        } else {
            publicaciones.forEach { (docId, pub) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(pub.titulo, style = MaterialTheme.typography.titleMedium)
                        Text(pub.descripcion, style = MaterialTheme.typography.bodyMedium)
                        if (pub.imagenUrl.isNotBlank()) {
                            Text("Imagen: ${pub.imagenUrl}", style = MaterialTheme.typography.bodySmall)
                        }
                        if (pub.newsurl.isNotBlank()) {
                            Text("Noticia: ${pub.newsurl}", style = MaterialTheme.typography.bodySmall)
                        }
                        Row(Modifier.padding(top = 8.dp)) {
                            Button(onClick = {
                                publicacionAEditar = docId to pub
                                showEditDialog = true
                            }) {
                                Text("Editar")
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = {
                                // Confirmar y eliminar
                                scope.launch {
                                    isLoading = true
                                    eliminarPublicacion(docId,
                                        onSuccess = {
                                            publicaciones = publicaciones.filterNot { it.first == docId }
                                            isLoading = false
                                        },
                                        onError = { e ->
                                            error = "Error al eliminar: ${e.message}"
                                            isLoading = false
                                        }
                                    )
                                }
                            }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                                Text("Eliminar", color = MaterialTheme.colorScheme.onError)
                            }
                        }
                    }
                }
            }
        }
        // Diálogo de edición
        if (showEditDialog && publicacionAEditar != null) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Editar publicación") },
                text = {
                    EditarPublicacionScreen(
                        publicacion = publicacionAEditar!!.second,
                        onGuardar = { nueva ->
                            scope.launch {
                                isLoading = true
                                actualizarPublicacion(
                                    docId = publicacionAEditar!!.first,
                                    nuevaPublicacion = nueva,
                                    onSuccess = {
                                        publicaciones = publicaciones.map {
                                            if (it.first == publicacionAEditar!!.first) it.first to nueva else it
                                        }
                                        isLoading = false
                                        showEditDialog = false
                                    },
                                    onError = { e ->
                                        error = "Error al actualizar: ${e.message}"
                                        isLoading = false
                                    }
                                )
                            }
                        }
                    )
                },
                confirmButton = {},
                dismissButton = {
                    OutlinedButton(onClick = { showEditDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
} 