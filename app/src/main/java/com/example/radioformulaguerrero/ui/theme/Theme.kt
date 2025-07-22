package com.example.radioformulaguerrero.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1E88E5),          // Azul principal
    secondary = Color(0xFF1565C0),        // Azul secundario
    tertiary = Color(0xFF0D47A1),         // Azul oscuro
    background = Color(0xFFFFFFFF),       // Fondo blanco
    surface = Color(0xFFF5F5F5),          // Superficie gris muy claro
    onPrimary = Color.White,              // Texto blanco sobre azul
    onSecondary = Color.White,            // Texto blanco sobre azul secundario
    onTertiary = Color.White,             // Texto blanco sobre azul oscuro
    onBackground = Color(0xFF1A1A1A),     // Texto casi negro sobre fondo blanco
    onSurface = Color(0xFF1A1A1A),        // Texto casi negro sobre superficie
    primaryContainer = Color(0xFFE3F2FD),  // Contenedor primario azul muy claro
    secondaryContainer = Color(0xFFBBDEFB), // Contenedor secundario azul claro
    surfaceVariant = Color(0xFFE3F2FD),    // Variante de superficie azul muy claro
    error = Color(0xFFE53935)              // Rojo para errores
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E88E5),          // Azul principal
    secondary = Color(0xFF1565C0),        // Azul secundario
    tertiary = Color(0xFF0D47A1),         // Azul oscuro
    background = Color(0xFFFFFFFF),       // Fondo blanco
    surface = Color(0xFFF5F5F5),          // Superficie gris muy claro
    onPrimary = Color.White,              // Texto blanco sobre azul
    onSecondary = Color.White,            // Texto blanco sobre azul secundario
    onTertiary = Color.White,             // Texto blanco sobre azul oscuro
    onBackground = Color(0xFF1A1A1A),     // Texto casi negro sobre fondo blanco
    onSurface = Color(0xFF1A1A1A),        // Texto casi negro sobre superficie
    primaryContainer = Color(0xFFE3F2FD),  // Contenedor primario azul muy claro
    secondaryContainer = Color(0xFFBBDEFB), // Contenedor secundario azul claro
    surfaceVariant = Color(0xFFE3F2FD),    // Variante de superficie azul muy claro
    error = Color(0xFFE53935)             // Rojo para errores
)

@Composable
fun RadioFormulaTheme(
    darkTheme: Boolean = false, // Mantenemos el tema claro por defecto
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}