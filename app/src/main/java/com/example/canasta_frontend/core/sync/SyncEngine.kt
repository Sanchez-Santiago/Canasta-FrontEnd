package com.example.canasta_frontend.core.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed interface SyncUiState<out T> {
    data object Loading : SyncUiState<Nothing>
    data class Content<T>(
        val data: T,
        val isSyncingInBackground: Boolean = false,
        val hasLocalData: Boolean = true
    ) : SyncUiState<T>
    data object Empty : SyncUiState<Nothing>
    data class Error(val message: String) : SyncUiState<Nothing>
}

object SyncEngine {
    fun <T> sync(
        fetchLocal: suspend () -> T?,
        fetchRemote: suspend () -> T?,
        saveLocal: suspend (T) -> Unit,
        isDataEmpty: (T) -> Boolean
    ): Flow<SyncUiState<T>> = flow {
        // 1. Initial check of local cache
        val local = try { fetchLocal() } catch (_: Exception) { null }
        var tieneDatosLocales = local != null && !isDataEmpty(local)

        if (tieneDatosLocales && local != null) {
            // Emite de inmediato los datos locales sin esperar a la red
            emit(SyncUiState.Content(data = local, isSyncingInBackground = true, hasLocalData = true))
        } else {
            // Muestra indicador de carga solo si no hay datos locales
            emit(SyncUiState.Loading)
        }

        // 2. Consulta en segundo plano a la API
        try {
            val remote = fetchRemote()
            if (remote != null && !isDataEmpty(remote)) {
                saveLocal(remote)
                tieneDatosLocales = true
                emit(SyncUiState.Content(data = remote, isSyncingInBackground = false, hasLocalData = true))
            } else if (remote != null && isDataEmpty(remote)) {
                if (!tieneDatosLocales) {
                    emit(SyncUiState.Empty)
                }
            } else if (!tieneDatosLocales) {
                emit(SyncUiState.Error("No fue posible cargar los datos."))
            }
        } catch (e: Exception) {
            if (!tieneDatosLocales) {
                emit(SyncUiState.Error(e.localizedMessage ?: "Error de conexión con el servidor."))
            }
        }
    }
}
