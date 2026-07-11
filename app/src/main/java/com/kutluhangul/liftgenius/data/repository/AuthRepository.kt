package com.kutluhangul.liftgenius.data.repository

import com.kutluhangul.liftgenius.domain.model.TrainerProfile
import com.kutluhangul.liftgenius.domain.model.TrainerRole
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val supabase: SupabaseClient,
) {

    /** Reactive auth state; drives top-level navigation (splash / auth / main). */
    val sessionStatus: StateFlow<SessionStatus> get() = supabase.auth.sessionStatus

    suspend fun signInWithEmail(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        ensureTrainerProfile()
    }

    /** @return true if a session was created; false when e-mail confirmation is pending. */
    suspend fun signUpWithEmail(fullName: String, email: String, password: String): Boolean {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject { put("full_name", fullName) }
        }
        val hasSession = supabase.auth.currentSessionOrNull() != null
        if (hasSession) ensureTrainerProfile(fullName)
        return hasSession
    }

    /** Google Sign-In via Credential Manager token (CLAUDE.md section 5, step 2). */
    suspend fun signInWithGoogle(googleIdToken: String) {
        supabase.auth.signInWith(IDToken) {
            idToken = googleIdToken
            provider = Google
        }
        ensureTrainerProfile()
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    fun currentUserId(): String? = supabase.auth.currentUserOrNull()?.id

    fun currentUserEmail(): String? = supabase.auth.currentUserOrNull()?.email

    /**
     * Creates the `trainer_profiles` row on first login if missing — auth.uid() ==
     * trainer_profiles.id (CLAUDE.md section 5). Idempotent; self-registered trainers
     * default to the `owner` role.
     */
    private suspend fun ensureTrainerProfile(fullNameHint: String? = null) {
        val user = supabase.auth.currentUserOrNull()
            ?: error("ensureTrainerProfile called without an authenticated user")
        val existing = supabase.from("trainer_profiles")
            .select { filter { eq("id", user.id) } }
            .decodeSingleOrNull<TrainerProfile>()
        if (existing != null) return

        val fullName = fullNameHint
            ?: (user.userMetadata?.get("full_name") as? JsonPrimitive)?.contentOrNull
            ?: user.email.orEmpty().substringBefore('@')
        supabase.from("trainer_profiles").insert(
            NewTrainerProfile(id = user.id, fullName = fullName, role = TrainerRole.OWNER),
        )
    }
}

/** Insert payload — `created_at` is set by the DB default (now()). */
@Serializable
private data class NewTrainerProfile(
    val id: String,
    @SerialName("full_name") val fullName: String,
    val role: TrainerRole,
)
