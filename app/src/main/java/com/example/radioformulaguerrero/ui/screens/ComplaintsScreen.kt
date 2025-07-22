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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import com.example.radioformulaguerrero.ui.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ComplaintsScreen(navController: NavController) {
    val viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    ComplaintsScreenContent(navController, viewModel)
}

@Composable
fun ComplaintsScreen(navController: NavController, viewModel: MainViewModel) {
    ComplaintsScreenContent(navController, viewModel)
}

@Composable
private fun ComplaintsScreenContent(navController: NavController, viewModel: MainViewModel) {
    val publicaciones by viewModel.publicaciones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var showForm by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var complaint by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quejas y Denuncias",
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

        // Botón para mostrar/ocultar formulario
        Button(
            onClick = { showForm = !showForm },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showForm) "Ocultar formulario" else "Nueva queja")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Formulario de queja
        if (showForm) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Fórmula abriendo la conversación",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Servicios sociales, quejas, denuncias, etc.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = complaint,
                        onValueChange = { complaint = it },
                        label = { Text("Mensaje") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(bottom = 16.dp),
                        maxLines = 6
                    )

                    Button(
                        onClick = { /* TODO: Implementar envío */ },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = name.isNotEmpty() && complaint.isNotEmpty()
                    ) {
                        Text("Enviar mensaje")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Lista de quejas existentes
        Text(
            text = "Quejas recientes",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

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
        // Lista de quejas
        else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (publicaciones.isEmpty()) {
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
                                    text = "No hay quejas disponibles",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Pulsa el botón de actualizar para cargar quejas",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(publicaciones) { publicacion ->
                        ComplaintCard(publicacion)
                    }
                }
            }
        }
    }
}

@Composable
fun ComplaintCard(publicacion: com.example.radioformulaguerrero.model.Publicacion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = publicacion.titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = publicacion.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = publicacion.fecha?.toDate()?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) } ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun sendComplaintEmail(name: String, complaint: String, context: Context): Boolean {
    return try {
        val sharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val appPassword = sharedPreferences.getString("app_password", null)
        val senderEmail = sharedPreferences.getString("sender_email", null)
        
        if (appPassword == null || appPassword.isEmpty()) {
            return false
        }
        
        if (senderEmail == null || senderEmail.isEmpty()) {
            return false
        }
        
        if (appPassword.length != 16) {
            return false
        }
        
        val username = senderEmail
        val password = appPassword

        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.ssl.trust", "smtp.gmail.com")
            put("mail.smtp.timeout", "10000")
            put("mail.smtp.connectiontimeout", "10000")
        }

        val session = Session.getInstance(props, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(username))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse("radioformula1@gmail.com"))
            subject = "Nueva queja de usuario"
            setText("Nombre: $name\n\nMensaje:\n$complaint")
        }
        
        Transport.send(message)
        true
    } catch (e: Exception) {
        false
    }
} 