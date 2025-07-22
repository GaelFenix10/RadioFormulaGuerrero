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
import com.example.radioformulaguerrero.ui.screens.PublicacionesScreen
import android.util.Log

@Composable
fun MainAuthNav() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var checkedAdmin by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Nuevo: Verificar usuario autenticado al iniciar
    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && !isLoggedIn && !checkedAdmin) {
            Log.d("AUTH", "Usuario ya autenticado: ${user.email}")
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    isLoggedIn = true
                    isAdmin = doc.getBoolean("isAdmin") == true
                    checkedAdmin = true
                    Log.d("AUTH", "isAdmin: $isAdmin, checkedAdmin: $checkedAdmin (auto-login)")
                }
                .addOnFailureListener {
                    error = "Error al verificar permisos."
                    Log.e("AUTH", "Error al verificar permisos (auto-login)", it)
                    FirebaseAuth.getInstance().signOut()
                }
        }
    }

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
                    Log.d("AUTH", "Intentando login para: $email")
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { authResult ->
                            val uid = authResult.user?.uid
                            Log.d("AUTH", "Login exitoso. UID: $uid")
                            if (uid != null) {
                                FirebaseFirestore.getInstance()
                                    .collection("usuarios")
                                    .document(uid)
                                    .get()
                                    .addOnSuccessListener { doc ->
                                        isLoading = false
                                        isLoggedIn = true
                                        isAdmin = doc.getBoolean("isAdmin") == true
                                        checkedAdmin = true
                                        Log.d("AUTH", "isAdmin: $isAdmin, checkedAdmin: $checkedAdmin")
                                    }
                                    .addOnFailureListener {
                                        isLoading = false
                                        error = "Error al verificar permisos."
                                        Log.e("AUTH", "Error al verificar permisos", it)
                                        FirebaseAuth.getInstance().signOut()
                                    }
                            } else {
                                isLoading = false
                                error = "Error de autenticación."
                                Log.e("AUTH", "UID nulo tras login")
                            }
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            error = "Correo o contraseña incorrectos."
                            Log.e("AUTH", "Login fallido", e)
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
    } else if (checkedAdmin) {
        Log.d("AUTH", "checkedAdmin: $checkedAdmin, isAdmin: $isAdmin")
        if (isAdmin) {
            Log.d("AUTH", "Mostrando AdminNav")
            AdminNav()
        } else {
            Log.d("AUTH", "Mostrando PublicacionesScreen (no admin)")
            // Pantalla de usuario normal (solo ver publicaciones, sin editar)
            PublicacionesScreen()
        }
    }
} 