package com.kutluhangul.liftgenius.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kutluhangul.liftgenius.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val sessionStatus: StateFlow<SessionStatus> = authRepository.sessionStatus

    private val _signOutError = MutableStateFlow<String?>(null)
    val signOutError: StateFlow<String?> = _signOutError.asStateFlow()

    fun currentUserEmail(): String? = authRepository.currentUserEmail()

    fun signOut() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _signOutError.value = e.message ?: "Çıkış yapılamadı."
            }
        }
    }
}
