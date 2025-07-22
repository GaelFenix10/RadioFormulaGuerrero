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
                                fecha = Timestamp.now(),
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
    }
} 