package com.example.radioformulaguerrero.ui.screens

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.example.radioformulaguerrero.ui.screens.AdminNav

@Composable
fun AdminAuthNav() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (!isLoggedIn) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    isLoading = true
                    error = null
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { authResult ->
                            val uid = authResult.user?.uid
                            if (uid != null) {
                                FirebaseFirestore.getInstance()
                                    .collection("usuarios")
                                    .document(uid)
                                    .get()
                                    .addOnSuccessListener { doc ->
                                        isLoading = false
                                        if (doc.getBoolean("isAdmin") == true) {
                                            isAdmin = true
                                            isLoggedIn = true
                                        } else {
                                            error = "No tienes permisos de administrador."
                                            FirebaseAuth.getInstance().signOut()
                                        }
                                    }
                                    .addOnFailureListener {
                                        isLoading = false
                                        error = "Error al verificar permisos."
                                        FirebaseAuth.getInstance().signOut()
                                    }
                            } else {
                                isLoading = false
                                error = "Error de autenticación."
                            }
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            error = "Correo o contraseña incorrectos."
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Entrar")
                }
            }
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    } else if (isAdmin) {
        // Aquí va tu panel de administración
        AdminNav()
    }
} 