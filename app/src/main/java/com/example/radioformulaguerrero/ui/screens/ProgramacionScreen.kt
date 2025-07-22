package com.example.radioformulaguerrero.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.radioformulaguerrero.data.obtenerProgramacion
import com.example.radioformulaguerrero.model.Programa

@Composable
fun ProgramacionScreen() {
    var lista by remember { mutableStateOf<List<Programa>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        cargando = true
        lista = obtenerProgramacion()
        cargando = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Programación",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (cargando) {
            CircularProgressIndicator()
        } else {
            lista.forEach { prog ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = prog.programa, style = MaterialTheme.typography.titleMedium)
                        Text(text = "${prog.horaInicio} - ${prog.horaFin}", style = MaterialTheme.typography.bodySmall)
                        Text(text = prog.descripcion, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
} 