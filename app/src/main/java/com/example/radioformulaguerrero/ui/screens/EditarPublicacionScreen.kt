package com.example.radioformulaguerrero.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.radioformulaguerrero.model.Publicacion

@Composable
fun EditarPublicacionScreen(
    publicacion: Publicacion = Publicacion(),
    onGuardar: (Publicacion) -> Unit
) {
    var titulo by remember { mutableStateOf(publicacion.titulo) }
    var descripcion by remember { mutableStateOf(publicacion.descripcion) }
    var imagenUrl by remember { mutableStateOf(publicacion.imagenUrl) }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = imagenUrl,
            onValueChange = { imagenUrl = it },
            label = { Text("URL de imagen") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                onGuardar(
                    publicacion.copy(
                        titulo = titulo,
                        descripcion = descripcion,
                        imagenUrl = imagenUrl
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Guardar")
        }
    }
} 