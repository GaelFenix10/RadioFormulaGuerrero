package com.example.radioformulaguerrero.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radioformulaguerrero.data.obtenerPublicaciones
import com.example.radioformulaguerrero.model.Publicacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    
    private val _publicaciones = MutableStateFlow<List<Publicacion>>(emptyList())
    val publicaciones: StateFlow<List<Publicacion>> = _publicaciones.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        cargarDatos()
    }
    
    fun cargarDatos() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Solo publicaciones
                _publicaciones.value = obtenerPublicaciones()

            } catch (e: Exception) {
                _error.value = "Error al cargar datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun limpiarError() {
        _error.value = null
    }
} 