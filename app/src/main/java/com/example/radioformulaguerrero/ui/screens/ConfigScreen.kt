package com.example.radioformulaguerrero.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    
    var senderEmail by remember { 
        mutableStateOf(sharedPreferences.getString("sender_email", "") ?: "") 
    }
    var appPassword by remember { 
        mutableStateOf(sharedPreferences.getString("app_password", "") ?: "") 
    }
    var isSaving by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Configuración de Correo",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Configuración de Envío",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Configura tu correo personal para enviar quejas a Radio Fórmula. Las quejas se enviarán a radioformula1@gmail.com.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Justify
                )
            }
        }

        OutlinedTextField(
            value = senderEmail,
            onValueChange = { senderEmail = it },
            label = { Text("Tu correo personal (Gmail)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        OutlinedTextField(
            value = appPassword,
            onValueChange = { appPassword = it },
            label = { Text("Contraseña de aplicación (16 caracteres)") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )

        if (resultMessage != null) {
            Text(
                text = resultMessage!!,
                color = if (resultMessage!!.contains("guardada")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                isSaving = true
                resultMessage = null
                
                if (senderEmail.isEmpty()) {
                    resultMessage = "Debes ingresar tu correo personal"
                } else if (appPassword.length != 16) {
                    resultMessage = "La contraseña debe tener exactamente 16 caracteres"
                } else {
                    sharedPreferences.edit()
                        .putString("sender_email", senderEmail)
                        .putString("app_password", appPassword)
                        .apply()
                    resultMessage = "Configuración guardada correctamente"
                }
                
                isSaving = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isSaving && senderEmail.isNotEmpty() && appPassword.isNotEmpty()
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Guardar configuración")
            }
        }

        TextButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Volver")
        }
    }
}

// --- DEPURACIÓN TEMPORAL: Revisar campos de la colección 'programacion' en Firestore ---
fun revisarProgramacion() {
    val db = FirebaseFirestore.getInstance()
    db.collection("programacion")
        .get()
        .addOnSuccessListener { snapshot ->
            for (doc in snapshot.documents) {
                val id = doc.id
                val programa = doc.get("programa")
                val horaInicio = doc.get("horaInicio")
                val horaFin = doc.get("horaFin")
                val descripcion = doc.get("descripcion")

                val errores = mutableListOf<String>()

                if (programa !is String) errores.add("programa mal o falta")
                if (horaInicio !is String) errores.add("horaInicio mal o falta")
                if (horaFin !is String) errores.add("horaFin mal o falta")
                if (descripcion !is String) errores.add("descripcion mal o falta")

                if (errores.isEmpty()) {
                    Log.i("RevisarProgramacion", "Documento $id: OK")
                } else {
                    Log.e("RevisarProgramacion", "Documento $id: ${errores.joinToString(", ")}")
                }
            }
        }
        .addOnFailureListener { e ->
            Log.e("RevisarProgramacion", "Error al leer Firestore: ${e.message}")
        }
} 