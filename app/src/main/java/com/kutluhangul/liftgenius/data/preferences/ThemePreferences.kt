package com.kutluhangul.liftgenius.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/** Persists the user's light/dark theme choice (Karanlık Mod toggle in Profile). Dark-first. */
@Singleton
class ThemePreferences @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val darkModeKey = booleanPreferencesKey("dark_mode")
    private val notificationsKey = booleanPreferencesKey("notifications_enabled")

    val isDarkMode: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[darkModeKey] ?: true
    }

    val notificationsEnabled: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[notificationsKey] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.settingsDataStore.edit { it[darkModeKey] = enabled }
    }

    suspend fun setNotifications(enabled: Boolean) {
        context.settingsDataStore.edit { it[notificationsKey] = enabled }
    }
}
