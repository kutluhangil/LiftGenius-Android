@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.data.repository

import com.kutluhangul.liftgenius.domain.model.NewSession
import com.kutluhangul.liftgenius.domain.model.Session
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Session scheduling (CLAUDE.md section 8). */
@Singleton
class SessionRepository @Inject constructor(
    private val supabase: SupabaseClient,
) {

    /** Sessions within a date window — calendar views. */
    suspend fun getSessionsBetween(from: Instant, to: Instant): List<Session> =
        supabase.from("sessions")
            .select {
                filter {
                    gte("date", from.toString())
                    lte("date", to.toString())
                }
                order("date", Order.ASCENDING)
            }
            .decodeList()

    suspend fun getSessionsForClient(clientId: String): List<Session> =
        supabase.from("sessions")
            .select {
                filter { eq("client_id", clientId) }
                order("date", Order.DESCENDING)
            }
            .decodeList()

    suspend fun addSession(draft: NewSession): Session =
        supabase.from("sessions")
            .insert(draft) { select() }
            .decodeSingle()

    suspend fun updateSession(session: Session): Session =
        supabase.from("sessions")
            .update(session) {
                filter { eq("id", session.id) }
                select()
            }
            .decodeSingle()

    suspend fun deleteSession(id: String) {
        supabase.from("sessions").delete {
            filter { eq("id", id) }
        }
    }
}
