package com.example.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiApiClient
import com.example.data.api.GeminiResult
import com.example.data.model.ActionCommand
import com.example.data.model.ActionType
import com.example.data.model.AssistantStatus
import com.example.data.model.CharacterAnimationState
import com.example.data.model.ChatMessage
import com.example.data.model.PendingAction
import com.example.data.prefs.AuraPreferences
import com.example.service.AndroidActionExecutor
import com.example.service.AuraAccessibilityService
import com.example.service.AuraAssistantService
import com.example.service.CommandInterpreter
import com.example.service.VoiceAssistantManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AuraScreen(val title: String) {
    HOME("Home"),
    AI_CHAT("AI Chat"),
    GEMINI_KEY("Gemini API Key"),
    VOICE_SETTINGS("Voice Settings"),
    FEMALE_VOICE("Female Voice"),
    LANGUAGE_SETTINGS("Language Settings"),
    PERMISSIONS("App Permissions"),
    ACCESSIBILITY_SETUP("Accessibility Setup"),
    BACKGROUND_MODE("Background Mode"),
    ANIMATION_SETTINGS("Animation Settings"),
    PRIVACY("Privacy & Security"),
    ABOUT("About YAKUB AURA AI")
}

class AuraViewModel(application: Application) : AndroidViewModel(application) {

    val prefs = AuraPreferences(application)
    private val actionExecutor = AndroidActionExecutor(application)
    private val geminiClient = GeminiApiClient()

    // Screen navigation
    private val _currentScreen = MutableStateFlow(AuraScreen.HOME)
    val currentScreen: StateFlow<AuraScreen> = _currentScreen.asStateFlow()

    // Assistant State
    private val _assistantStatus = MutableStateFlow(AssistantStatus.READY)
    val assistantStatus: StateFlow<AssistantStatus> = _assistantStatus.asStateFlow()

    private val _animationState = MutableStateFlow(CharacterAnimationState.IDLE)
    val animationState: StateFlow<CharacterAnimationState> = _animationState.asStateFlow()

    // Chat history
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Pending confirmation dialog
    private val _pendingAction = MutableStateFlow<PendingAction?>(null)
    val pendingAction: StateFlow<PendingAction?> = _pendingAction.asStateFlow()

    // Quick status feedback notification
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Connection testing state
    private val _apiTestResult = MutableStateFlow<String?>(null)
    val apiTestResult: StateFlow<String?> = _apiTestResult.asStateFlow()

    private val _isTestingApi = MutableStateFlow(false)
    val isTestingApi: StateFlow<Boolean> = _isTestingApi.asStateFlow()

    private var voiceManager: VoiceAssistantManager? = null

    val isListening: StateFlow<Boolean> get() = voiceManager?.isListening ?: MutableStateFlow(false)
    val rmsDb: StateFlow<Float> get() = voiceManager?.rmsDb ?: MutableStateFlow(0f)
    val availableVoices get() = voiceManager?.availableVoices ?: MutableStateFlow(emptyList())

    init {
        _chatMessages.value = prefs.loadChatHistory()
        initVoiceManager()
    }

    private fun initVoiceManager() {
        voiceManager = VoiceAssistantManager(
            context = getApplication(),
            onSpeechRecognized = { text ->
                handleUserInput(text)
            },
            onSpeakingStateChanged = { isSpeaking ->
                if (isSpeaking) {
                    _assistantStatus.value = AssistantStatus.SPEAKING
                    _animationState.value = CharacterAnimationState.SPEAKING
                } else {
                    if (_assistantStatus.value != AssistantStatus.PAUSED) {
                        _assistantStatus.value = AssistantStatus.READY
                        _animationState.value = CharacterAnimationState.IDLE
                    }
                }
            },
            onError = { err ->
                setStatusMessage(err)
                _assistantStatus.value = AssistantStatus.READY
                _animationState.value = CharacterAnimationState.IDLE
            }
        )
        syncVoicePrefs()
    }

    fun syncVoicePrefs() {
        voiceManager?.setupVoicePreferences(
            languagePref = prefs.voiceLanguage,
            rate = prefs.speechRate,
            pitch = prefs.speechPitch,
            preferredVoiceName = prefs.selectedVoiceName
        )
    }

    fun navigateTo(screen: AuraScreen) {
        _currentScreen.value = screen
    }

    fun setStatusMessage(msg: String?) {
        _statusMessage.value = msg
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun dismissPendingAction() {
        _pendingAction.value = null
    }

    fun toggleListening() {
        if (_assistantStatus.value == AssistantStatus.PAUSED) {
            _assistantStatus.value = AssistantStatus.READY
            _animationState.value = CharacterAnimationState.IDLE
        }

        if (voiceManager?.isListening?.value == true) {
            voiceManager?.stopListening()
            _assistantStatus.value = AssistantStatus.READY
            _animationState.value = CharacterAnimationState.IDLE
        } else {
            voiceManager?.stopSpeaking()
            _assistantStatus.value = AssistantStatus.LISTENING
            _animationState.value = CharacterAnimationState.LISTENING
            voiceManager?.startListening(prefs.voiceLanguage)
        }
    }

    fun pauseAssistant() {
        voiceManager?.stopListening()
        voiceManager?.stopSpeaking()
        _assistantStatus.value = AssistantStatus.PAUSED
        _animationState.value = CharacterAnimationState.PAUSED
        setStatusMessage("Assistant Paused")
    }

    fun stopSpeaking() {
        voiceManager?.stopSpeaking()
        _assistantStatus.value = AssistantStatus.READY
        _animationState.value = CharacterAnimationState.IDLE
    }

    fun handleUserInput(rawInput: String) {
        if (rawInput.isBlank()) return
        val userMsg = ChatMessage(text = rawInput, isUser = true)
        val updatedList = _chatMessages.value + userMsg
        _chatMessages.value = updatedList
        prefs.saveChatHistory(updatedList)

        val cmd = CommandInterpreter.interpret(rawInput)
        executeCommand(cmd)
    }

    private fun executeCommand(cmd: ActionCommand) {
        when (cmd.type) {
            ActionType.OPEN_APP -> {
                val success = actionExecutor.launchApp(cmd.target)
                if (success) {
                    respondWithBaby(cmd.spokenResponse, "Opened ${cmd.target}")
                } else {
                    val appName = when (cmd.target) {
                        "com.google.android.youtube" -> "YouTube"
                        "com.whatsapp" -> "WhatsApp"
                        "com.facebook.katana" -> "Facebook"
                        "org.telegram.messenger" -> "Telegram"
                        else -> cmd.target
                    }
                    respondWithBaby(
                        "Baby, $appName is not installed on this device. You can download it from the Play Store.",
                        "App not found: $appName"
                    )
                }
            }

            ActionType.SEARCH_YOUTUBE -> {
                val success = actionExecutor.searchYouTube(cmd.query)
                if (success) {
                    respondWithBaby(cmd.spokenResponse, "YouTube search: ${cmd.query}")
                } else {
                    respondWithBaby("Baby, I couldn't open YouTube for search.", "YouTube search failed")
                }
            }

            ActionType.MEDIA_CONTROL -> {
                val success = actionExecutor.controlMedia(cmd.target)
                if (success) {
                    respondWithBaby(cmd.spokenResponse, "Media action: ${cmd.target}")
                } else {
                    respondWithBaby("Baby, no active media player responded to the command.", "Media control unavailable")
                }
            }

            ActionType.OPEN_SETTINGS -> {
                actionExecutor.openSettings()
                respondWithBaby(cmd.spokenResponse, "Settings opened")
            }

            ActionType.CALL_CONTACT -> {
                val contactName = cmd.target
                val matches = actionExecutor.findContacts(contactName)
                if (matches.isEmpty()) {
                    respondWithBaby(
                        "Baby, I couldn't find any contact named $contactName in your phone book. Please make sure Contacts permission is granted.",
                        "Contact not found: $contactName"
                    )
                } else if (matches.size == 1) {
                    val contact = matches.first()
                    _pendingAction.value = PendingAction(
                        title = "Confirm Phone Call",
                        description = "Do you want to call ${contact.name} (${contact.phoneNumber})?",
                        type = ActionType.CALL_CONTACT,
                        onConfirm = {
                            actionExecutor.dialPhoneNumber(contact.phoneNumber)
                            _pendingAction.value = null
                            respondWithBaby("Calling ${contact.name} now, Baby!", "Dialed ${contact.phoneNumber}")
                        }
                    )
                    voiceManager?.speak("Baby, do you want to call ${contact.name}? Please tap confirm.")
                } else {
                    val firstName = matches.first()
                    _pendingAction.value = PendingAction(
                        title = "Multiple Contacts Found",
                        description = "Found ${matches.size} contacts for '$contactName'. Call ${firstName.name} (${firstName.phoneNumber})?",
                        type = ActionType.CALL_CONTACT,
                        onConfirm = {
                            actionExecutor.dialPhoneNumber(firstName.phoneNumber)
                            _pendingAction.value = null
                            respondWithBaby("Calling ${firstName.name}, Baby!", "Dialed ${firstName.phoneNumber}")
                        }
                    )
                    voiceManager?.speak("Baby, I found multiple contacts matching $contactName. Please confirm who to call.")
                }
            }

            ActionType.SEND_MESSAGE -> {
                val contactName = cmd.target
                val messageText = cmd.secondaryData
                val matches = actionExecutor.findContacts(contactName)
                val targetPhone = matches.firstOrNull()?.phoneNumber ?: ""

                _pendingAction.value = PendingAction(
                    title = "Confirm Send Message",
                    description = "Send message to $contactName${if (targetPhone.isNotEmpty()) " ($targetPhone)" else ""}:\n\n\"$messageText\"",
                    type = ActionType.SEND_MESSAGE,
                    onConfirm = {
                        actionExecutor.composeSms(targetPhone, messageText)
                        _pendingAction.value = null
                        respondWithBaby("Opening your messaging app with the text prepared, Baby!", "SMS drafted for $contactName")
                    }
                )
                voiceManager?.speak("Baby, I prepared your message for $contactName. Please confirm before sending.")
            }

            ActionType.AUTOMATION_SCREEN -> {
                val isServiceActive = AuraAccessibilityService.isServiceActive.value
                if (!isServiceActive) {
                    respondWithBaby(
                        "Baby, Screen Automation requires Accessibility Service to be enabled in your phone Settings.",
                        "Accessibility Service not enabled"
                    )
                    return
                }

                val executed = when (cmd.target) {
                    "GLOBAL_BACK" -> AuraAccessibilityService.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK)
                    "GLOBAL_HOME" -> AuraAccessibilityService.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME)
                    "GLOBAL_NOTIFICATIONS" -> AuraAccessibilityService.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
                    "SCROLL_DOWN" -> AuraAccessibilityService.scrollScreen(true)
                    "SCROLL_UP" -> AuraAccessibilityService.scrollScreen(false)
                    else -> false
                }

                if (executed) {
                    respondWithBaby(cmd.spokenResponse, "Screen action: ${cmd.target}")
                } else {
                    respondWithBaby("Baby, the screen action couldn't be completed on this screen.", "Automation failed")
                }
            }

            ActionType.CONVERSATION -> {
                queryGemini(cmd.query)
            }
        }
    }

    private fun queryGemini(prompt: String) {
        _assistantStatus.value = AssistantStatus.THINKING
        _animationState.value = CharacterAnimationState.THINKING

        viewModelScope.launch {
            val result = geminiClient.generateResponse(
                apiKey = prefs.apiKey,
                userPrompt = prompt,
                history = _chatMessages.value
            )
            when (result) {
                is GeminiResult.Success -> {
                    respondWithBaby(result.data, "AI Response")
                }
                is GeminiResult.Error -> {
                    _assistantStatus.value = AssistantStatus.READY
                    _animationState.value = CharacterAnimationState.IDLE
                    setStatusMessage(result.message)
                    respondWithBaby(
                        "Baby, I encountered an issue: ${result.message}",
                        "Error: ${result.message}"
                    )
                }
            }
        }
    }

    private fun respondWithBaby(text: String, actionSummary: String? = null) {
        val aiMsg = ChatMessage(text = text, isUser = false, actionSummary = actionSummary)
        val updated = _chatMessages.value + aiMsg
        _chatMessages.value = updated
        prefs.saveChatHistory(updated)

        voiceManager?.speak(text)
    }

    fun testApiConnection(keyToTest: String) {
        _isTestingApi.value = true
        _apiTestResult.value = null

        viewModelScope.launch {
            val result = geminiClient.testConnection(keyToTest)
            _isTestingApi.value = false
            when (result) {
                is GeminiResult.Success -> {
                    _apiTestResult.value = "Success! Gemini connection verified."
                }
                is GeminiResult.Error -> {
                    _apiTestResult.value = "Failed: ${result.message}"
                }
            }
        }
    }

    fun openAiStudioKeyPage() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/apikey")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            getApplication<Application>().startActivity(intent)
        } catch (_: Exception) {}
    }

    fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            getApplication<Application>().startActivity(intent)
        } catch (_: Exception) {}
    }

    fun toggleBackgroundMode(enabled: Boolean) {
        prefs.backgroundModeEnabled = enabled
        val context = getApplication<Application>()
        val serviceIntent = Intent(context, AuraAssistantService::class.java)
        if (enabled) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            setStatusMessage("Background assistant active")
        } else {
            context.stopService(serviceIntent)
            setStatusMessage("Background assistant stopped")
        }
    }

    fun clearHistory() {
        _chatMessages.value = emptyList()
        prefs.clearChatHistory()
        setStatusMessage("Chat history cleared")
    }

    fun clearSavedApiKey() {
        prefs.clearApiKey()
        _apiTestResult.value = null
        setStatusMessage("API key removed")
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager?.release()
    }
}
