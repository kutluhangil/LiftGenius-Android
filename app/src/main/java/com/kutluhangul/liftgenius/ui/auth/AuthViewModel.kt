package com.kutluhangul.liftgenius.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val awaitingEmailConfirmation: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "E-posta ve şifre gerekli.") }
            return
        }
        launchAuth {
            authRepository.signInWithEmail(trimmedEmail, password)
            // Navigation happens reactively via sessionStatus (MainActivity).
        }
    }

    fun register(fullName: String, email: String, password: String) {
        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim()
        if (trimmedName.isBlank() || trimmedEmail.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Tüm alanları doldur.") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Şifre en az 6 karakter olmalı.") }
            return
        }
        launchAuth {
            val signedIn = authRepository.signUpWithEmail(trimmedName, trimmedEmail, password)
            if (!signedIn) {
                // E-mail confirmation flow — same behavior as iOS (CLAUDE.md section 5).
                _uiState.update { it.copy(awaitingEmailConfirmation = true) }
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        launchAuth {
            authRepository.signInWithGoogle(idToken)
            // Navigation happens reactively via sessionStatus (MainActivity).
        }
    }

    fun showError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    fun clearError() {
        if (_uiState.value.errorMessage != null) {
            _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun launchAuth(block: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                block()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: e::class.simpleName ?: "Bilinmeyen hata",
                    )
                }
            }
        }
    }
}
