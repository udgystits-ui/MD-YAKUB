package com.example.data.model

enum class AssistantStatus(val label: String) {
    READY("Ready"),
    LISTENING("Listening..."),
    THINKING("Thinking..."),
    SPEAKING("Speaking..."),
    PAUSED("Paused")
}

enum class CharacterAnimationState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    GREETING,
    RESPONDING,
    PAUSED
}

enum class ActionType {
    OPEN_APP,
    SEARCH_YOUTUBE,
    CALL_CONTACT,
    SEND_MESSAGE,
    OPEN_SETTINGS,
    MEDIA_CONTROL,
    AUTOMATION_SCREEN,
    CONVERSATION
}

data class ActionCommand(
    val type: ActionType,
    val target: String = "",
    val query: String = "",
    val secondaryData: String = "",
    val spokenResponse: String = ""
)

data class PendingAction(
    val title: String,
    val description: String,
    val type: ActionType,
    val onConfirm: () -> Unit
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val actionSummary: String? = null
)
