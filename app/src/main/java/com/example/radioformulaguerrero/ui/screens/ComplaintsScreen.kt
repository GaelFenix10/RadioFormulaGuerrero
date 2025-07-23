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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.sp
import android.util.Log

@Composable
fun ComplaintsScreen(navController: NavController) {
    Log.d("Pantalla", "Entrando a ComplaintsScreen")
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    val role = sharedPreferences.getString("role", "user")
    if (role == "admin") {
        AdminDenunciasScreen()
    } else {
        UserDenunciaScreen(context, sharedPreferences)
    }
}

@Composable
fun UserDenunciaScreen(context: android.content.Context, sharedPreferences: android.content.SharedPreferences) {
    var showForm by remember { mutableStateOf(false) }
    var titular by remember { mutableStateOf("") }
    var detalle by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var sendError by remember { mutableStateOf<String?>(null) }
    var sendSuccess by remember { mutableStateOf(false) }
    val correo = sharedPreferences.getString("email", "") ?: ""
    val telefono = sharedPreferences.getString("telefono", "") ?: ""
    val nombre = sharedPreferences.getString("nombre", "") ?: ""
    val fechaNacimiento = sharedPreferences.getString("fechaNacimiento", "") ?: ""
    val edad = sharedPreferences.getString("edad", "") ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Quejas y Denuncias", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { showForm = !showForm }, modifier = Modifier.fillMaxWidth()) {
            Text(if (showForm) "Ocultar formulario" else "Nueva denuncia")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (showForm) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = titular,
                        onValueChange = { titular = it },
                        label = { Text("Titular") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = detalle,
                        onValueChange = { detalle = it },
                        label = { Text("Detalle de la denuncia") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 6
                    )
                    if (sendError != null) {
                        Text(sendError!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                    }
                    if (sendSuccess) {
                        Text("Denuncia enviada correctamente", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
                    }
                    Button(
                        onClick = {
                            isSending = true
                            sendError = null
                            sendSuccess = false
                            val db = FirebaseFirestore.getInstance()
                            val denuncia = hashMapOf(
                                "titular" to titular,
                                "detalle" to detalle,
                                "correo" to correo,
                                "telefono" to telefono,
                                "nombre" to nombre,
                                "fechaNacimiento" to fechaNacimiento,
                                "edad" to edad,
                                "fecha" to com.google.firebase.Timestamp.now()
                            )
                            db.collection("denuncias").add(denuncia)
                                .addOnSuccessListener {
                                    isSending = false
                                    sendSuccess = true
                                    titular = ""
                                    detalle = ""
                                }
                                .addOnFailureListener { e ->
                                    isSending = false
                                    sendError = "Error al enviar denuncia: ${e.message}"
                                }
                        },
                        enabled = titular.isNotBlank() && detalle.isNotBlank() && !isSending,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text("Enviar denuncia")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminDenunciasScreen() {
    var denuncias by remember { mutableStateOf<List<Map<String, Any?>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val correosCache = remember { mutableStateMapOf<String, String>() }
    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("denuncias").orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING).get().await()
            denuncias = snapshot.documents.map { doc ->
                val data = doc.data ?: emptyMap()
                val mutable = data.toMutableMap()
                mutable["id"] = doc.id
                mutable
            }
        } catch (e: Exception) {
            error = "Error al cargar denuncias: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Denuncias recibidas", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        } else if (denuncias.isEmpty()) {
            Text("No hay denuncias")
        } else {
            denuncias.forEachIndexed { idx, denuncia ->
                var expanded by remember { mutableStateOf(false) }
                var correo by remember { mutableStateOf(denuncia["email"] as? String ?: denuncia["correo"] as? String ?: "") }
                val userId = denuncia["id"] as? String
                // Si no hay correo, buscarlo en usernoadmin
                LaunchedEffect(expanded) {
                    if (expanded && correo.isBlank() && userId != null && !correosCache.containsKey(userId)) {
                        try {
                            val db = FirebaseFirestore.getInstance()
                            val userDoc = db.collection("usernoadmin").document(userId).get().await()
                            val userEmail = userDoc.getString("email") ?: "-"
                            correosCache[userId] = userEmail
                            correo = userEmail
                        } catch (_: Exception) {
                            correo = "-"
                        }
                    } else if (expanded && userId != null && correosCache.containsKey(userId)) {
                        correo = correosCache[userId] ?: "-"
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { expanded = !expanded },
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            denuncia["titular"] as? String ?: "(Sin titular)",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)
                        )
                        if (expanded) {
                            Text("Numero de denuncia: ${idx + 1}", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
                            Text("ID: ${denuncia["id"] ?: "-"}", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
                            Text("Fecha: ${(denuncia["fecha"] as? com.google.firebase.Timestamp)?.toDate()?.toString() ?: "-"}", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
                            Text("Nombre: ${denuncia["nombre"] ?: "-"}", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
                            Text("Teléfono: ${denuncia["telefono"] ?: "-"}", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
                            Text("Fecha de nacimiento: ${denuncia["fechaNacimiento"] ?: "-"}", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
                            Text("Edad: ${denuncia["edad"] ?: "-"}", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
                            Text("Noticia detallada: ${denuncia["detalle"] ?: "-"}", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
                        } else {
                            Text("(Toca para ver más detalles)", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp), color = MaterialTheme.colorScheme.primary)
                        }
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