package com.example.radioformulaguerrero.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import android.view.View

/**
 * Configuraciones de rendimiento para la aplicación
 */
object PerformanceConfig {
    const val LAZY_COLUMN_ITEM_COUNT = 20
    const val IMAGE_CACHE_SIZE = 100
    const val NETWORK_TIMEOUT = 10000L // 10 segundos
}

/**
 * Optimización de rendimiento para vistas Compose
 */
@Composable
fun PerformanceOptimizations() {
    val view = LocalView.current
    
    DisposableEffect(view) {
        // Configurar optimizaciones de hardware
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        
        onDispose {
            // Limpiar recursos al destruir
            view.setLayerType(View.LAYER_TYPE_NONE, null)
        }
    }
} 