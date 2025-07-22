package com.example.radioformulaguerrero.ui.screens

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import com.example.radioformulaguerrero.model.Publicacion
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import android.util.Log

@Composable
fun PublicacionesScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(false) }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf<String?>(null) }

    // Verificar si el usuario es admin (por UID en Firestore)
    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val doc = FirebaseFirestore.getInstance().collection("usuarios").document(user.uid).get().await()
            isAdmin = doc.getBoolean("isAdmin") == true
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // El Card de creación de publicaciones ha sido removido
            Text("Pantalla de publicaciones (solo ver)")
            // Aquí puedes mostrar la lista de publicaciones reales
        }
        // Botón flotante solo para admin (ya no se usa, pero lo dejo comentado)
        // if (isAdmin) {
        //     FloatingActionButton(
        //         onClick = { showDialog = true },
        //         modifier = Modifier
        //             .align(Alignment.BottomEnd)
        //             .padding(16.dp)
        //     ) {
        //         Icon(Icons.Default.Add, contentDescription = "Agregar publicación")
        //     }
        // }
        // Formulario modal para agregar publicación (ya no se usa)
        // if (showDialog) { ... }
    }
}

@Composable
fun AddPublicacionDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar publicación") },
        text = {
            Column {
                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") })
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
                OutlinedTextField(value = imagenUrl, onValueChange = { imagenUrl = it }, label = { Text("URL de imagen (opcional)") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (titulo.isNotBlank() && descripcion.isNotBlank()) {
                    onAdd(titulo, descripcion, imagenUrl)
                }
            }) {
                Text("Agregar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
} 