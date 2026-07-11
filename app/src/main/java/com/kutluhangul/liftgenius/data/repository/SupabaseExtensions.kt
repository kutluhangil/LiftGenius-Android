package com.kutluhangul.liftgenius.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth

/** The authenticated user's id == trainer_id in the shared schema (CLAUDE.md section 5). */
internal fun SupabaseClient.requireUserId(): String =
    auth.currentUserOrNull()?.id
        ?: error("No authenticated user. Repository calls require a signed-in trainer.")
