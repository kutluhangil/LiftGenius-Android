@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.data.repository

import com.kutluhangul.liftgenius.domain.model.Client
import com.kutluhangul.liftgenius.domain.model.ClientPr
import com.kutluhangul.liftgenius.domain.model.ClientProgress
import com.kutluhangul.liftgenius.domain.model.NewClient
import com.kutluhangul.liftgenius.domain.model.NewClientPr
import com.kutluhangul.liftgenius.domain.model.NewClientProgress
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.ExperimentalTime

/** Clients + progress measurements + personal records (CLAUDE.md section 8). */
@Singleton
class ClientRepository @Inject constructor(
    private val supabase: SupabaseClient,
) {

    // ── clients ──

    suspend fun getClients(): List<Client> =
        supabase.from("clients")
            .select {
                order("created_at", Order.DESCENDING)
            }
            .decodeList()

    suspend fun getClient(id: String): Client =
        supabase.from("clients")
            .select {
                filter { eq("id", id) }
            }
            .decodeSingle()

    suspend fun addClient(draft: NewClient): Client =
        supabase.from("clients")
            .insert(draft.copy(trainerId = supabase.requireUserId())) { select() }
            .decodeSingle()

    suspend fun updateClient(client: Client): Client =
        supabase.from("clients")
            .update(client) {
                filter { eq("id", client.id) }
                select()
            }
            .decodeSingle()

    suspend fun deleteClient(id: String) {
        supabase.from("clients").delete {
            filter { eq("id", id) }
        }
    }

    // ── client_progress ──

    suspend fun getProgress(clientId: String): List<ClientProgress> =
        supabase.from("client_progress")
            .select {
                filter { eq("client_id", clientId) }
                order("date", Order.DESCENDING)
            }
            .decodeList()

    suspend fun addProgress(draft: NewClientProgress): ClientProgress =
        supabase.from("client_progress")
            .insert(draft) { select() }
            .decodeSingle()

    suspend fun deleteProgress(id: String) {
        supabase.from("client_progress").delete {
            filter { eq("id", id) }
        }
    }

    // ── client_prs ──

    suspend fun getPrs(clientId: String): List<ClientPr> =
        supabase.from("client_prs")
            .select {
                filter { eq("client_id", clientId) }
                order("date", Order.DESCENDING)
            }
            .decodeList()

    suspend fun addPr(draft: NewClientPr): ClientPr =
        supabase.from("client_prs")
            .insert(draft) { select() }
            .decodeSingle()

    suspend fun deletePr(id: String) {
        supabase.from("client_prs").delete {
            filter { eq("id", id) }
        }
    }
}
