package com.example.radioformulaguerrero.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.radioformulaguerrero.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import com.example.radioformulaguerrero.ui.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Add
import coil.compose.AsyncImage
import androidx.compose.ui.unit.sp
import com.example.radioformulaguerrero.data.obtenerProgramacionGenerica
import kotlinx.coroutines.launch

@Composable
fun ScheduleScreen(navController: NavController) {
    val viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    ScheduleScreenContent(navController, viewModel)
}

@Composable
fun ScheduleScreen(navController: NavController, viewModel: MainViewModel) {
    ScheduleScreenContent(navController, viewModel)
}

@Composable
private fun ScheduleScreenContent(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    var programacion by remember { mutableStateOf<List<Map<String, Any?>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(false) }

    // Verificar si el usuario es admin (por UID en Firestore)
    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val doc = FirebaseFirestore.getInstance().collection("usuarios").document(user.uid).get().await()
            isAdmin = doc.getBoolean("isAdmin") == true
        }
    }

    // Obtener programación genérica
    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        try {
            programacion = obtenerProgramacionGenerica()
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Header con botón de refresh
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Programación",
                    style = MaterialTheme.typography.headlineMedium
                )
                IconButton(
                    onClick = {
                        isLoading = true
                        error = null
                        scope.launch {
                            try {
                                programacion = obtenerProgramacionGenerica()
                            } catch (e: Exception) {
                                error = e.message
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                isLoading = true
                                error = null
                                scope.launch {
                                    try {
                                        programacion = obtenerProgramacionGenerica()
                                    } catch (e: Exception) {
                                        error = e.message
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (programacion.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No hay programación disponible",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Pulsa el botón de actualizar para cargar la programación",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(programacion) { prog ->
                            ProgramCardGenerica(prog)
                        }
                    }
                }
            }
        }

        // Botón flotante solo para admin
        if (isAdmin) {
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar programa")
            }
        }

        // Formulario modal para agregar programa
        if (showDialog) {
            AddProgramaDialog(
                onDismiss = { showDialog = false },
                onAdd = { programa, horaInicio, horaFin, descripcion ->
                    val db = FirebaseFirestore.getInstance()
                    val nuevo = com.example.radioformulaguerrero.model.Programa(
                        programa = programa,
                        horaInicio = horaInicio,
                        horaFin = horaFin,
                        descripcion = descripcion
                    )
                    db.collection("programacion").add(nuevo)
                        .addOnSuccessListener {
                            viewModel.cargarDatos()
                            showDialog = false
                        }
                        .addOnFailureListener {
                            showDialog = false
                        }
                }
            )
        }
    }
}

@Composable
fun ProgramCardGenerica(prog: Map<String, Any?>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val imagenUrl = prog["imagenUrl"] as? String ?: ""
            if (imagenUrl.isNotBlank()) {
                AsyncImage(
                    model = imagenUrl,
                    contentDescription = "Imagen del programa",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            val titulo = prog["programa"] as? String ?: prog["id"] as? String ?: "(Sin título)"
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            prog.forEach { (key, value) ->
                if (key != "imagenUrl" && key != "programa" && key != "id" && value is String && value.isNotBlank()) {
                    Text(
                        text = "$key: $value",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddProgramaDialog(onDismiss: () -> Unit, onAdd: (String, String, String, String) -> Unit) {
    var programa by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar programa") },
        text = {
            Column {
                OutlinedTextField(value = programa, onValueChange = { programa = it }, label = { Text("Programa") })
                OutlinedTextField(value = horaInicio, onValueChange = { horaInicio = it }, label = { Text("Hora inicio") })
                OutlinedTextField(value = horaFin, onValueChange = { horaFin = it }, label = { Text("Hora fin") })
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (programa.isNotBlank() && horaInicio.isNotBlank() && horaFin.isNotBlank()) {
                    onAdd(programa, horaInicio, horaFin, descripcion)
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

// Mantener la clase Program para compatibilidad si se necesita
data class Program(
    val name: String,
    val schedule: String,
    val days: String
) 