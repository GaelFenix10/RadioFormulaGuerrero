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
    val programacion by viewModel.programacion.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
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
                    onClick = { viewModel.cargarDatos() },
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Estado de carga
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            // Estado de error
            else if (error != null) {
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
                                viewModel.limpiarError()
                                viewModel.cargarDatos()
                            }
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            // Contenido principal
            else {
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
                        items(programacion) { programa ->
                            ProgramCard(programa)
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
fun ProgramCard(programa: com.example.radioformulaguerrero.model.Programa) {
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
            Text(
                text = programa.programa,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${programa.horaInicio} - ${programa.horaFin}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (programa.descripcion.isNotEmpty()) {
                Text(
                    text = programa.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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