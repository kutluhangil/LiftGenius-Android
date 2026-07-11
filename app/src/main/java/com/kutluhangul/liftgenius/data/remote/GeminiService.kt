package com.kutluhangul.liftgenius.data.remote

import com.kutluhangul.liftgenius.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google Gemini text generation (CLAUDE.md section 6). External to the shared backend —
 * used to draft workout/nutrition plans which are then saved to Supabase.
 */
@Singleton
class GeminiService @Inject constructor() {

    private val json = Json { ignoreUnknownKeys = true }
    private val client = HttpClient(Android)

    suspend fun generate(prompt: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            error(
                "Gemini API anahtarı yapılandırılmamış. local.properties dosyasına " +
                    "GEMINI_API_KEY ekleyip yeniden derle (CLAUDE.md bölüm 6).",
            )
        }
        val response = client.post(ENDPOINT) {
            parameter("key", apiKey)
            contentType(ContentType.Application.Json)
            setBody(
                json.encodeToString(
                    GeminiRequest.serializer(),
                    GeminiRequest(listOf(GeminiContent(listOf(GeminiPart(prompt))))),
                ),
            )
        }
        val bodyText = response.bodyAsText()
        if (!response.status.isSuccess()) {
            error("Gemini isteği başarısız (${response.status}): ${bodyText.take(300)}")
        }
        val parsed = json.decodeFromString(GeminiResponse.serializer(), bodyText)
        return parsed.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: error("Gemini boş yanıt döndürdü: ${bodyText.take(300)}")
    }

    companion object {
        private const val ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"

        /** Pulls the first JSON object out of a model response (handles ```json fences). */
        fun extractJson(text: String): String {
            val fenced = Regex("```(?:json)?\\s*([\\s\\S]*?)\\s*```")
                .find(text)
                ?.groupValues
                ?.get(1)
            val candidate = fenced ?: text
            val start = candidate.indexOf('{')
            val end = candidate.lastIndexOf('}')
            if (start == -1 || end <= start) {
                error("Gemini yanıtında JSON bulunamadı: ${text.take(200)}")
            }
            return candidate.substring(start, end + 1)
        }
    }
}

@Serializable
private data class GeminiRequest(val contents: List<GeminiContent>)

@Serializable
private data class GeminiContent(val parts: List<GeminiPart> = emptyList())

@Serializable
private data class GeminiPart(val text: String = "")

@Serializable
private data class GeminiResponse(val candidates: List<GeminiCandidate>? = null)

@Serializable
private data class GeminiCandidate(val content: GeminiContent? = null)
