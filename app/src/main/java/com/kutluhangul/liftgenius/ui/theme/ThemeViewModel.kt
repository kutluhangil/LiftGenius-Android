package com.kutluhangul.liftgenius.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.preferences.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreferences: ThemePreferences,
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = themePreferences.isDarkMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = true,
    )

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { themePreferences.setDarkMode(enabled) }
    }
}
