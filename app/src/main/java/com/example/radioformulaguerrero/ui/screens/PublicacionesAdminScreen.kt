package com.example.radioformulaguerrero.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.radioformulaguerrero.model.Publicacion

@Composable
fun PublicacionesAdminScreen(
    publicaciones: List<Pair<String, Publicacion>>,
    onEditar: (String, Publicacion) -> Unit,
    onEliminar: (String) -> Unit,
    onAgregar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = onAgregar,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Agregar publicación")
        }
        LazyColumn {
            items(publicaciones) { (docId, pub) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(pub.titulo, style = MaterialTheme.typography.titleMedium)
                        Text(pub.descripcion, style = MaterialTheme.typography.bodyMedium)
                        Row(Modifier.padding(top = 8.dp)) {
                            Button(onClick = { onEditar(docId, pub) }) {
                                Text("Editar")
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { onEliminar(docId) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
} 