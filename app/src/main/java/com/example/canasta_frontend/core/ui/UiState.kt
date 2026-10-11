package com.example.canasta_frontend.core.ui

sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>
    data class Contenido<T>(
        val data: T,
        val sincronizando: Boolean = false
    ) : UiState<T>
    data object Vacio : UiState<Nothing>
    data class Error(val mensaje: String) : UiState<Nothing>
}
