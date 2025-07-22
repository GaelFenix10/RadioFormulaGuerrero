package com.example.radioformulaguerrero.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.radioformulaguerrero.ui.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
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