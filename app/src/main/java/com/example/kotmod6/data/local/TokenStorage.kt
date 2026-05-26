package com.example.kotmod6.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.tokenDataStore by preferencesDataStore("server_auth")

class TokenStorage(
    private val context: Context
) {
    private val key = stringPreferencesKey("jwt")

    val tokenFlow: Flow<String?> = context.tokenDataStore.data.map { preferences ->
        preferences[key]
    }

    suspend fun save(token: String) {
        context.tokenDataStore.edit { preferences ->
            preferences[key] = token
        }
    }

    suspend fun clear() {
        context.tokenDataStore.edit { preferences ->
            preferences.remove(key)
        }
    }
}
