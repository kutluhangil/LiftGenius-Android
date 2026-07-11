@file:OptIn(ExperimentalTime::class)

package com.kutluhangul.liftgenius.data.repository

import com.kutluhangul.liftgenius.domain.model.Exercise
import com.kutluhangul.liftgenius.domain.model.NewExercise
import com.kutluhangul.liftgenius.domain.model.NewWorkoutDay
import com.kutluhangul.liftgenius.domain.model.NewWorkoutPlan
import com.kutluhangul.liftgenius.domain.model.WorkoutDay
import com.kutluhangul.liftgenius.domain.model.WorkoutPlan
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.ExperimentalTime

/** Workout plan → day → exercise hierarchy (CLAUDE.md section 8). */
@Singleton
class WorkoutRepository @Inject constructor(
    private val supabase: SupabaseClient,
) {

    suspend fun getPlans(clientId: String): List<WorkoutPlan> =
        supabase.from("workout_plans")
            .select {
                filter { eq("client_id", clientId) }
                order("created_at", Order.DESCENDING)
            }
            .decodeList()

    suspend fun getDays(planId: String): List<WorkoutDay> =
        supabase.from("workout_days")
            .select {
                filter { eq("plan_id", planId) }
                order("order_index", Order.ASCENDING)
            }
            .decodeList()

    suspend fun getExercises(dayId: String): List<Exercise> =
        supabase.from("exercises")
            .select {
                filter { eq("day_id", dayId) }
                order("order_index", Order.ASCENDING)
            }
            .decodeList()

    suspend fun addPlan(draft: NewWorkoutPlan): WorkoutPlan =
        supabase.from("workout_plans")
            .insert(draft.copy(trainerId = supabase.requireUserId())) { select() }
            .decodeSingle()

    suspend fun addDay(draft: NewWorkoutDay): WorkoutDay =
        supabase.from("workout_days")
            .insert(draft) { select() }
            .decodeSingle()

    suspend fun addExercises(drafts: List<NewExercise>): List<Exercise> =
        supabase.from("exercises")
            .insert(drafts) { select() }
            .decodeList()

    /**
     * Deletes a plan with its days and exercises. Explicit bottom-up chain — does not rely
     * on DB-side cascade rules.
     */
    suspend fun deletePlan(planId: String) {
        val dayIds = getDays(planId).map { it.id }
        if (dayIds.isNotEmpty()) {
            supabase.from("exercises").delete {
                filter { isIn("day_id", dayIds) }
            }
            supabase.from("workout_days").delete {
                filter { eq("plan_id", planId) }
            }
        }
        supabase.from("workout_plans").delete {
            filter { eq("id", planId) }
        }
    }
}
