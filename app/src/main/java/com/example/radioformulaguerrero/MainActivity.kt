package com.example.radioformulaguerrero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.radioformulaguerrero.ui.screens.*
import com.example.radioformulaguerrero.ui.theme.RadioFormulaTheme
import com.example.radioformulaguerrero.ui.theme.PerformanceOptimizations
import com.google.firebase.FirebaseApp
import com.example.radioformulaguerrero.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // FirebaseApp.initializeApp(this) // Eliminado porque ya se inicializa en MyApplication.kt
        
        // Configuraciones de rendimiento
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        setContent {
            RadioFormulaTheme { 
                PerformanceOptimizations()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RadioFormulaApp()
                }
            }
        }
    }
}

@Composable
fun RadioFormulaApp() {
    val navController = rememberNavController()
    AppNavigation(navController = navController)
}