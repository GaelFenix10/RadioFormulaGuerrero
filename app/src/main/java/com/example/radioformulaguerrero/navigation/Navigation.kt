package com.example.radioformulaguerrero.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.radioformulaguerrero.ui.screens.*
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
fun AppNavigation(navController: NavHostController) {
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        startDestination = if (user != null) "home" else "login"
    }

    // Esperar a que se determine el destino inicial
    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination!!
        ) {
            composable("home") {
                MainScreen(navController = navController)
            }
            composable("radio") {
                RadioScreen(navController = navController)
            }
            composable("schedule") {
                ScheduleScreen(navController = navController)
            }
            composable("complaints") {
                ComplaintsScreen(navController = navController)
            }
            composable("mainauthnav") {
                MainAuthNav()
            }
            composable("login") {
                LoginScreen(navController = navController)
            }
            composable("register") {
                RegisterScreen(navController = navController)
            }
            composable("config") {
                ConfigScreen(navController = navController)
            }
        }
    }
} 