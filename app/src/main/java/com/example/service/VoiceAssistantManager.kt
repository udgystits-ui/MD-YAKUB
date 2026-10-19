package com.example.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class VoiceAssistantManager(
    private val context: Context,
    private val onSpeechRecognized: (String) -> Unit,
    private val onSpeakingStateChanged: (isSpeaking: Boolean) -> Unit,
    private val onError: (String) -> Unit
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var isTtsInitialized = false

    private val _rmsDb = MutableStateFlow(0f)
    val rmsDb: StateFlow<Float> = _rmsDb

    private val _availableVoices = MutableStateFlow<List<Voice>>(emptyList())
    val availableVoices: StateFlow<List<Voice>> = _availableVoices

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    init {
        tts = TextToSpeech(context, this)
        initSpeechRecognizer()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            setupVoicePreferences()
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    onSpeakingStateChanged(true)
                }

                override fun onDone(utteranceId: String?) {
                    onSpeakingStateChanged(false)
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    onSpeakingStateChanged(false)
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    onSpeakingStateChanged(false)
                }
            })
        } else {
            isTtsInitialized = false
            onError("Text-to-Speech initialization failed on device.")
        }
    }

    fun setupVoicePreferences(
        languagePref: String = "auto",
        rate: Float = 1.0f,
        pitch: Float = 1.15f,
        preferredVoiceName: String = ""
    ) {
        val engine = tts ?: return
        if (!isTtsInitialized) return

        engine.setSpeechRate(rate)
        engine.setPitch(pitch)

        try {
            val voices = engine.voices?.toList() ?: emptyList()
            _availableVoices.value = voices

            // Try to find female voice
            var selectedVoice: Voice? = null
            if (preferredVoiceName.isNotEmpty()) {
                selectedVoice = voices.find { it.name == preferredVoiceName }
            }

            if (selectedVoice == null) {
                // Find a female voice matching language preference
                val targetLocale = when (languagePref) {
                    "bn" -> Locale("bn", "BD")
                    "en" -> Locale.US
                    else -> Locale.getDefault()
                }
                selectedVoice = voices.find { v ->
                    (v.name.contains("female", ignoreCase = true) || v.name.contains("#female", ignoreCase = true) || v.name.contains("f00", ignoreCase = true)) &&
                            (v.locale.language == targetLocale.language)
                } ?: voices.find { it.locale.language == targetLocale.language }
            }

            if (selectedVoice != null) {
                engine.voice = selectedVoice
            } else {
                val loc = if (languagePref == "bn") Locale("bn", "BD") else Locale.US
                engine.language = loc
            }
        } catch (_: Exception) {
            engine.language = Locale.US
        }
    }

    fun speak(text: String, utteranceId: String = "AURA_UTTERANCE") {
        if (!isTtsInitialized) {
            onError("Voice engine is not initialized yet.")
            return
        }
        val cleanText = text.replace(Regex("[*#_`~]"), "").trim()
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stopSpeaking() {
        tts?.stop()
        onSpeakingStateChanged(false)
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                    }

                    override fun onBeginningOfSpeech() {}

                    override fun onRmsChanged(rmsdB: Float) {
                        _rmsDb.value = (rmsdB / 10f).coerceIn(0f, 1f)
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                        _rmsDb.value = 0f
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        _rmsDb.value = 0f
                        val message = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
                            SpeechRecognizer.ERROR_CLIENT -> "Speech recognition client error."
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
                            SpeechRecognizer.ERROR_NETWORK -> "Network error during speech recognition."
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout."
                            SpeechRecognizer.ERROR_NO_MATCH -> "I didn't quite catch that, Baby. Please speak again."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech service is busy."
                            SpeechRecognizer.ERROR_SERVER -> "Speech server error."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected."
                            else -> "Speech recognition error code: $error"
                        }
                        // Only report non-trivial errors
                        if (error != SpeechRecognizer.ERROR_NO_MATCH && error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                            onError(message)
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        _rmsDb.value = 0f
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()
                        if (!text.isNullOrBlank()) {
                            onSpeechRecognized(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
    }

    fun startListening(languagePref: String = "auto") {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition is not available on this device.")
            return
        }
        stopSpeaking()
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            val langTag = when (languagePref) {
                "bn" -> "bn-BD"
                "en" -> "en-US"
                else -> Locale.getDefault().toLanguageTag()
            }
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langTag)
        }
        try {
            speechRecognizer?.startListening(intent)
            _isListening.value = true
        } catch (e: Exception) {
            _isListening.value = false
            onError("Could not start listening: ${e.localizedMessage}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        _isListening.value = false
        _rmsDb.value = 0f
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        speechRecognizer?.destroy()
    }
}
