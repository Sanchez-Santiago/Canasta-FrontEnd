package com.example.canasta_frontend.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class GroupSelection(
    val modoGrupo: String = "personal",
    val activeGroupId: Long? = null,
    val groupName: String = "Canasta Personal"
)

class GroupPreferences(private val context: Context) {
    companion object {
        val MODO_GRUPO_KEY = stringPreferencesKey("modo_grupo")
        val ACTIVE_GROUP_ID_KEY = longPreferencesKey("active_group_id")
        val GROUP_NAME_KEY = stringPreferencesKey("group_name")
    }

    val selectionFlow: Flow<GroupSelection> = context.dataStore.data.map { prefs ->
        GroupSelection(
            modoGrupo = prefs[MODO_GRUPO_KEY] ?: "personal",
            activeGroupId = prefs[ACTIVE_GROUP_ID_KEY],
            groupName = prefs[GROUP_NAME_KEY] ?: "Canasta Personal"
        )
    }

    suspend fun saveSelection(modoGrupo: String, activeGroupId: Long?, groupName: String) {
        context.dataStore.edit { prefs ->
            prefs[MODO_GRUPO_KEY] = modoGrupo
            if (activeGroupId != null) prefs[ACTIVE_GROUP_ID_KEY] = activeGroupId
            else prefs.remove(ACTIVE_GROUP_ID_KEY)
            prefs[GROUP_NAME_KEY] = groupName
        }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(MODO_GRUPO_KEY)
            prefs.remove(ACTIVE_GROUP_ID_KEY)
            prefs.remove(GROUP_NAME_KEY)
        }
    }
}
