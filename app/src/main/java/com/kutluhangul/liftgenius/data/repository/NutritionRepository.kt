@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.data.repository

import com.kutluhangul.liftgenius.domain.model.NewNutritionPlan
import com.kutluhangul.liftgenius.domain.model.NutritionPlan
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.ExperimentalTime

/** Nutrition plans (CLAUDE.md section 8). */
@Singleton
class NutritionRepository @Inject constructor(
    private val supabase: SupabaseClient,
) {

    suspend fun getPlans(clientId: String): List<NutritionPlan> =
        supabase.from("nutrition_plans")
            .select {
                filter { eq("client_id", clientId) }
                order("created_at", Order.DESCENDING)
            }
            .decodeList()

    suspend fun addPlan(draft: NewNutritionPlan): NutritionPlan =
        supabase.from("nutrition_plans")
            .insert(draft.copy(trainerId = supabase.requireUserId())) { select() }
            .decodeSingle()

    suspend fun updatePlan(plan: NutritionPlan): NutritionPlan =
        supabase.from("nutrition_plans")
            .update(plan) {
                filter { eq("id", plan.id) }
                select()
            }
            .decodeSingle()

    suspend fun deletePlan(id: String) {
        supabase.from("nutrition_plans").delete {
            filter { eq("id", id) }
        }
    }
}
