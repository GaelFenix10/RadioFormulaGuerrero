package com.example.radioformulaguerrero.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.radioformulaguerrero.R
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.radioformulaguerrero.ui.viewmodels.MainViewModel
import androidx.compose.ui.Alignment
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    Log.d("Pantalla", "Entrando a MainScreen")
    val viewModel: MainViewModel = viewModel()
    var selectedTab by remember { mutableStateOf("home") }
    var isLoggedIn by remember { mutableStateOf(false) }
    var showAccountMenu by remember { mutableStateOf(false) }
    var userEmail by remember { mutableStateOf("") }

    val tabs = listOf(
        TabItem("Inicio", Icons.Default.Home, "home"),
        TabItem("Radio", Icons.Default.Radio, "radio"),
        TabItem("Programación", Icons.Default.Schedule, "schedule"),
        TabItem("Quejas", Icons.Default.Report, "complaints"),
        TabItem("Perfil", Icons.Default.AccountCircle, "profile")
    )

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo Radio Formula Guerrero",
                            modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth()
                        )
                    },
                    // Eliminamos el actions con el icono de perfil
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color(0xFF1A1A1A)
                    ),
                    modifier = Modifier.statusBarsPadding()
                )
                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                tab.icon, 
                                contentDescription = tab.title,
                                modifier = Modifier.size(24.dp)
                            ) 
                        },
                        label = { 
                            Text(
                                tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selectedTab == tab.route) 
                                        FontWeight.Bold 
                                    else 
                                        FontWeight.Normal
                                )
                            ) 
                        },
                        selected = selectedTab == tab.route,
                        onClick = {
                            selectedTab = tab.route
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                "home" -> HomeScreen(viewModel)
                "radio" -> RadioScreen(navController)
                "schedule" -> ProgramacionScreen()
                "complaints" -> ComplaintsScreen(navController)
                "profile" -> ProfileScreen(navController)
                else -> HomeScreen(viewModel)
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    val role = sharedPreferences.getString("role", "user")
    val nombre = sharedPreferences.getString("nombre", "")
    val fechaNacimiento = sharedPreferences.getString("fechaNacimiento", "")
    val edad = sharedPreferences.getString("edad", "")
    val correo = sharedPreferences.getString("email", "")
    val telefono = sharedPreferences.getString("telefono", "")
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (role == "admin") {
                Text("Bienvenido, administrador", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("mainauthnav") }) {
                    Text("Publicaciones")
                }
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Text("Bienvenido a tu perfil", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Nombre: $nombre", style = MaterialTheme.typography.bodyLarge)
                Text("Fecha de nacimiento: $fechaNacimiento", style = MaterialTheme.typography.bodyLarge)
                Text("Edad: $edad", style = MaterialTheme.typography.bodyLarge)
                Text("Correo: $correo", style = MaterialTheme.typography.bodyLarge)
                Text("Teléfono: $telefono", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(24.dp))
            }
            Button(onClick = {
                FirebaseAuth.getInstance().signOut()
                sharedPreferences.edit().clear().apply()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Cerrar sesión", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}

private data class TabItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) 