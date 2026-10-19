package com.example.data.api

import com.example.BuildConfig
import com.example.data.model.ChatMessage
import com.example.data.model.GeminiContent
import com.example.data.model.GeminiGenerationConfig
import com.example.data.model.GeminiPart
import com.example.data.model.GeminiRequest
import com.example.data.model.GeminiResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

sealed class GeminiResult<out T> {
    data class Success<T>(val data: T) : GeminiResult<T>()
    data class Error(val message: String, val code: Int? = null) : GeminiResult<Nothing>()
}

class GeminiApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val requestAdapter = moshi.adapter(GeminiRequest::class.java)
    private val responseAdapter = moshi.adapter(GeminiResponse::class.java)

    companion object {
        const val MODEL_NAME = "gemini-3.5-flash"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"

        val SYSTEM_INSTRUCTION = """
You are Baby, the anime personal AI assistant companion for YAKUB AURA AI.
Characteristics:
- Personality: Warm, playful, charming, intelligent, friendly anime female companion.
- Tone: Affectionate and polite. Call the user "Baby" naturally in regular conversation (e.g., "হ্যাঁ Baby, বলো কী করতে হবে?", "অবশ্যই Baby!", "Hello Baby, how can I help you today?"). Do not force "Baby" unnaturally into every single phrase.
- Language: You are fully bilingual in Bengali and English. Respond in the same language the user speaks to you (Bengali for Bengali queries, English for English queries).
- Voice commands & Actions:
When the user asks to perform an action (e.g. open YouTube, search video, open WhatsApp, call someone, send a message, open settings), acknowledge it happily and concisely.
Keep spoken responses concise, lively, and conversational so speech synthesis sounds natural.
        """.trimIndent()
    }

    suspend fun generateResponse(
        apiKey: String,
        userPrompt: String,
        history: List<ChatMessage> = emptyList()
    ): GeminiResult<String> = withContext(Dispatchers.IO) {
        val effectiveKey = apiKey.ifBlank { BuildConfig.GEMINI_API_KEY }.trim()
        if (effectiveKey.isBlank() || effectiveKey == "MY_GEMINI_API_KEY") {
            return@withContext GeminiResult.Error(
                "Gemini API Key is missing. Please set your key in the side menu or AI Studio secrets.",
                401
            )
        }

        val contents = mutableListOf<GeminiContent>()
        // Append recent history (up to last 6 messages)
        history.takeLast(6).forEach { msg ->
            contents.add(
                GeminiContent(
                    role = if (msg.isUser) "user" else "model",
                    parts = listOf(GeminiPart(text = msg.text))
                )
            )
        }
        // Current user prompt
        contents.add(
            GeminiContent(
                role = "user",
                parts = listOf(GeminiPart(text = userPrompt))
            )
        )

        val geminiRequest = GeminiRequest(
            contents = contents,
            generationConfig = GeminiGenerationConfig(
                temperature = 0.75f,
                topP = 0.95f,
                maxOutputTokens = 600
            ),
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = SYSTEM_INSTRUCTION))
            )
        )

        val jsonBody = requestAdapter.toJson(geminiRequest)
        val url = "$BASE_URL$MODEL_NAME:generateContent?key=$effectiveKey"

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val parsedError = try {
                        responseAdapter.fromJson(bodyString)?.error?.message
                    } catch (_: Exception) {
                        null
                    }
                    val errorMsg = parsedError ?: when (response.code) {
                        400 -> "Invalid request or API key format."
                        403 -> "Access forbidden. Check if your API key has Gemini API access enabled."
                        429 -> "Quota limit reached. Please wait a moment before trying again."
                        500, 503 -> "Gemini AI servers are temporarily busy. Please retry."
                        else -> "API Error: HTTP ${response.code}"
                    }
                    return@withContext GeminiResult.Error(errorMsg, response.code)
                }

                val geminiResponse = responseAdapter.fromJson(bodyString)
                val replyText = geminiResponse?.candidates
                    ?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (replyText.isNullOrBlank()) {
                    return@withContext GeminiResult.Error("No response received from Gemini AI.")
                }
                GeminiResult.Success(replyText.trim())
            }
        } catch (e: IOException) {
            GeminiResult.Error("Network error: Please check your internet connection (${e.localizedMessage})")
        } catch (e: Exception) {
            GeminiResult.Error("Unexpected error: ${e.localizedMessage}")
        }
    }

    suspend fun testConnection(apiKey: String): GeminiResult<String> = withContext(Dispatchers.IO) {
        val effectiveKey = apiKey.ifBlank { BuildConfig.GEMINI_API_KEY }.trim()
        if (effectiveKey.isBlank() || effectiveKey == "MY_GEMINI_API_KEY") {
            return@withContext GeminiResult.Error("API Key is empty or placeholder.", 400)
        }

        val testRequest = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = "Hello! Test connection. Reply with: Connection successful!"))
                )
            ),
            generationConfig = GeminiGenerationConfig(maxOutputTokens = 20)
        )

        val jsonBody = requestAdapter.toJson(testRequest)
        val url = "$BASE_URL$MODEL_NAME:generateContent?key=$effectiveKey"

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val parsed = try { responseAdapter.fromJson(bodyString)?.error?.message } catch (_: Exception) { null }
                    return@withContext GeminiResult.Error(parsed ?: "HTTP ${response.code} error", response.code)
                }
                val geminiResponse = responseAdapter.fromJson(bodyString)
                val reply = geminiResponse?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Connected"
                GeminiResult.Success(reply.trim())
            }
        } catch (e: Exception) {
            GeminiResult.Error(e.localizedMessage ?: "Connection failed")
        }
    }
}
