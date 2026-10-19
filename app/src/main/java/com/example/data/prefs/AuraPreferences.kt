package com.example.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.ChatMessage
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class AuraPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("aura_ai_preferences", Context.MODE_PRIVATE)

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val chatListType = Types.newParameterizedType(List::class.java, ChatMessage::class.java)
    private val chatAdapter = moshi.adapter<List<ChatMessage>>(chatListType)

    var apiKey: String
        get() = prefs.getString("gemini_api_key", "") ?: ""
        set(value) = prefs.edit().putString("gemini_api_key", value.trim()).apply()

    var speechRate: Float
        get() = prefs.getFloat("speech_rate", 1.0f)
        set(value) = prefs.edit().putFloat("speech_rate", value).apply()

    var speechPitch: Float
        get() = prefs.getFloat("speech_pitch", 1.15f)
        set(value) = prefs.edit().putFloat("speech_pitch", value).apply()

    var voiceLanguage: String
        get() = prefs.getString("voice_language", "auto") ?: "auto"
        set(value) = prefs.edit().putString("voice_language", value).apply()

    var selectedVoiceName: String
        get() = prefs.getString("selected_voice_name", "") ?: ""
        set(value) = prefs.edit().putString("selected_voice_name", value).apply()

    var animationQuality: String
        get() = prefs.getString("animation_quality", "High") ?: "High"
        set(value) = prefs.edit().putString("animation_quality", value).apply()

    var backgroundModeEnabled: Boolean
        get() = prefs.getBoolean("bg_mode_enabled", false)
        set(value) = prefs.edit().putBoolean("bg_mode_enabled", value).apply()

    var onboardingCompleted: Boolean
        get() = prefs.getBoolean("onboarding_completed", false)
        set(value) = prefs.edit().putBoolean("onboarding_completed", value).apply()

    fun saveChatHistory(messages: List<ChatMessage>) {
        try {
            val json = chatAdapter.toJson(messages.takeLast(100))
            prefs.edit().putString("chat_history", json).apply()
        } catch (_: Exception) {}
    }

    fun loadChatHistory(): List<ChatMessage> {
        val json = prefs.getString("chat_history", null) ?: return emptyList()
        return try {
            chatAdapter.fromJson(json) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun clearChatHistory() {
        prefs.edit().remove("chat_history").apply()
    }

    fun clearApiKey() {
        prefs.edit().remove("gemini_api_key").apply()
    }

    fun resetAllSettings() {
        prefs.edit().clear().apply()
    }
}
