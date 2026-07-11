@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.data.repository

import com.kutluhangul.liftgenius.domain.model.ClientPackage
import com.kutluhangul.liftgenius.domain.model.NewClientPackage
import com.kutluhangul.liftgenius.domain.model.TrainerProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.ExperimentalTime

/** Packages/memberships + trainer/salon profiles — finance & team (CLAUDE.md section 8). */
@Singleton
class BusinessRepository @Inject constructor(
    private val supabase: SupabaseClient,
) {

    // ── packages ──

    /** All packages visible to this trainer (RLS-scoped) — finance overview. */
    suspend fun getAllPackages(): List<ClientPackage> =
        supabase.from("packages")
            .select {
                order("start_date", Order.DESCENDING)
            }
            .decodeList()

    suspend fun getPackages(clientId: String): List<ClientPackage> =
        supabase.from("packages")
            .select {
                filter { eq("client_id", clientId) }
                order("start_date", Order.DESCENDING)
            }
            .decodeList()

    suspend fun addPackage(draft: NewClientPackage): ClientPackage =
        supabase.from("packages")
            .insert(draft) { select() }
            .decodeSingle()

    suspend fun updatePackage(pkg: ClientPackage): ClientPackage =
        supabase.from("packages")
            .update(pkg) {
                filter { eq("id", pkg.id) }
                select()
            }
            .decodeSingle()

    suspend fun deletePackage(id: String) {
        supabase.from("packages").delete {
            filter { eq("id", id) }
        }
    }

    // ── trainer_profiles ──

    suspend fun getOwnProfile(): TrainerProfile =
        supabase.from("trainer_profiles")
            .select {
                filter { eq("id", supabase.requireUserId()) }
            }
            .decodeSingle()

    /** Team members visible to this account (RLS-scoped). */
    suspend fun getTeam(): List<TrainerProfile> =
        supabase.from("trainer_profiles")
            .select {
                order("created_at", Order.ASCENDING)
            }
            .decodeList()

    suspend fun updateProfile(profile: TrainerProfile): TrainerProfile =
        supabase.from("trainer_profiles")
            .update(profile) {
                filter { eq("id", profile.id) }
                select()
            }
            .decodeSingle()
}
