package com.example.radioformulaguerrero.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.radioformulaguerrero.data.obtenerProgramacionGenerica
import coil.compose.AsyncImage
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.animation.animateContentSize
import android.util.Log

@Composable
fun ProgramacionScreen() {
    Log.d("Pantalla", "Entrando a ProgramacionScreen")
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    val isAdmin = sharedPreferences.getString("role", "user") == "admin"
    var eventos by remember { mutableStateOf<List<Map<String, Any?>>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editEvento by remember { mutableStateOf<Map<String, Any?>?>(null) }

    // Cargar eventos de la colección 'eventos'
    fun cargarEventos() {
        cargando = true
        error = null
        scope.launch {
            try {
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val eventosSnap = db.collection("eventos").orderBy("hora").get().await()
                eventos = eventosSnap.documents.map { doc ->
                    val data = doc.data ?: emptyMap()
                    val mutable = data.toMutableMap()
                    mutable["id"] = doc.id
                    mutable
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                cargando = false
            }
        }
    }

    LaunchedEffect(Unit) { cargarEventos() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Programación",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // Card 1: Marco Antonio Aguileta (estilo NewsCard)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .border(1.dp, Color(0xFFB0B0B0), shape = MaterialTheme.shapes.medium)
                .padding(bottom = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Marco Antonio Aguileta (15:00 hrs)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    ),
                    color = Color(0xFF232526)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Espacio periodístico con la información más relevante de Guerrero y el país, entrevistas y análisis para que estés al día. Sintoniza a las 15:00 hrs.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF414345)
                )
            }
        }
        // Card 2: Varinka Pinto (estilo NewsCard)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .border(1.dp, Color(0xFFB0B0B0), shape = MaterialTheme.shapes.medium)
                .padding(bottom = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Varinka Pinto (16:30 hrs)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    ),
                    color = Color(0xFF232526)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Revista radiofónica ligera y cercana: entrevistas, cultura local, servicio social y notas de interés. ‘En Sintonía’ inicia a las 16:30 hrs.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF414345)
                )
            }
        }
        // Sección de eventos
        Spacer(modifier = Modifier.height(24.dp))
        Text("Eventos", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
        if (cargando) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else {
            eventos.forEach { evento ->
                var expanded by remember { mutableStateOf(false) }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { expanded = !expanded },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = evento["titulo"] as? String ?: "(Sin título)",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)
                        )
                        if (expanded) {
                            // Mostrar solo los valores de los campos relevantes, sin títulos
                            listOf(
                                evento["descripcion"] as? String,
                                evento["hora"] as? String
                            ).filter { !it.isNullOrBlank() }.forEach { valor ->
                                Text(
                                    text = valor!!,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                                )
                            }
                            if (isAdmin) {
                                Row(Modifier.padding(top = 8.dp)) {
                                    Button(onClick = { editEvento = evento }) {
                                        Text("Editar")
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Button(onClick = {
                                        // Eliminar evento
                                        scope.launch {
                                            try {
                                                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                                db.collection("eventos").document(evento["id"] as String).delete().await()
                                                cargarEventos()
                                            } catch (e: Exception) {
                                                error = e.message
                                            }
                                        }
                                    }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                                        Text("Eliminar", color = MaterialTheme.colorScheme.onError)
                                    }
                                }
                            }
                        } else {
                            Text("(Toca para ver detalles)", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp), color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
        if (isAdmin) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showAddDialog = true }) {
                Text("Agregar evento")
            }
        }
    }
    // Diálogo para agregar o editar evento
    if (showAddDialog || editEvento != null) {
        var titulo by remember { mutableStateOf(editEvento?.get("titulo") as? String ?: "") }
        var descripcion by remember { mutableStateOf(editEvento?.get("descripcion") as? String ?: "") }
        var hora by remember { mutableStateOf(editEvento?.get("hora") as? String ?: "") }
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                editEvento = null
            },
            title = { Text(if (editEvento != null) "Editar evento" else "Agregar evento") },
            text = {
                Column {
                    OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") })
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
                    OutlinedTextField(value = hora, onValueChange = { hora = it }, label = { Text("Hora") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        try {
                            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            if (editEvento != null) {
                                db.collection("eventos").document(editEvento!!["id"] as String)
                                    .set(mapOf("titulo" to titulo, "descripcion" to descripcion, "hora" to hora)).await()
                            } else {
                                db.collection("eventos").add(mapOf("titulo" to titulo, "descripcion" to descripcion, "hora" to hora)).await()
                            }
                            cargarEventos()
                            showAddDialog = false
                            editEvento = null
                        } catch (e: Exception) {
                            error = e.message
                        }
                    }
                }) {
                    Text(if (editEvento != null) "Guardar cambios" else "Agregar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showAddDialog = false
                    editEvento = null
                }) { Text("Cancelar") }
            }
        )
    }
} 