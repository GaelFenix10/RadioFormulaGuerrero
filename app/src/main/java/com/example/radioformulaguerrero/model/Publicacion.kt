package com.example.radioformulaguerrero.model

data class Publicacion(
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: com.google.firebase.Timestamp? = null,
    val imagenUrl: String = "",
    val newsurl: String = ""
) 