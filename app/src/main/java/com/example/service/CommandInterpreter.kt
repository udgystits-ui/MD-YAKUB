package com.example.service

import com.example.data.model.ActionCommand
import com.example.data.model.ActionType

object CommandInterpreter {

    fun interpret(input: String): ActionCommand {
        val raw = input.trim()
        val cleanRaw = raw
            .replace("(?i)^baby[,\\s]*".toRegex(), "")
            .replace("(?i)^বেবি[,\\s]*".toRegex(), "")
            .trim()
        val text = cleanRaw.lowercase()

        // 1. YouTube Commands
        if (text.contains("open youtube") || text.contains("youtube open") || text.contains("ইউটিউব খোলো") || text == "youtube") {
            return ActionCommand(
                type = ActionType.OPEN_APP,
                target = "com.google.android.youtube",
                spokenResponse = "Opening YouTube for you, Baby!"
            )
        }

        if (text.startsWith("search for") || text.startsWith("search") || text.startsWith("find") ||
            text.contains("play a music video") || text.contains("search youtube") || text.contains("ইউটিউবে খুঁজুন") ||
            text.contains("gaan search koro") || text.contains("video dekhao")
        ) {
            var query = cleanRaw
                .replace("(?i)search for".toRegex(), "")
                .replace("(?i)search on youtube for".toRegex(), "")
                .replace("(?i)search youtube for".toRegex(), "")
                .replace("(?i)search".toRegex(), "")
                .replace("(?i)find".toRegex(), "")
                .replace("(?i)on youtube".toRegex(), "")
                .replace("(?i)gaan search koro".toRegex(), "")
                .replace("(?i)video dekhao".toRegex(), "")
                .trim()
            if (query.isEmpty() || query.equals("a music video", ignoreCase = true)) query = "trending songs"
            return ActionCommand(
                type = ActionType.SEARCH_YOUTUBE,
                query = query,
                spokenResponse = "Searching YouTube for $query, Baby!"
            )
        }

        // 2. Media Controls
        if (text.contains("pause the video") || text.contains("pause video") || text.contains("pause music") || text == "pause") {
            return ActionCommand(
                type = ActionType.MEDIA_CONTROL,
                target = "PAUSE",
                spokenResponse = "Pausing playback, Baby."
            )
        }
        if (text.contains("resume playback") || text.contains("resume video") || text.contains("play video") || text == "resume") {
            return ActionCommand(
                type = ActionType.MEDIA_CONTROL,
                target = "RESUME",
                spokenResponse = "Resuming playback, Baby."
            )
        }
        if (text.contains("play next video") || text.contains("next song") || text.contains("next video")) {
            return ActionCommand(
                type = ActionType.MEDIA_CONTROL,
                target = "NEXT",
                spokenResponse = "Skipping to the next track, Baby."
            )
        }

        // 3. App Launchers
        if (text.contains("open whatsapp") || text.contains("হোয়াটসঅ্যাপ খোলো")) {
            return ActionCommand(
                type = ActionType.OPEN_APP,
                target = "com.whatsapp",
                spokenResponse = "Opening WhatsApp, Baby."
            )
        }
        if (text.contains("open facebook") || text.contains("ফেসবুক খোলো")) {
            return ActionCommand(
                type = ActionType.OPEN_APP,
                target = "com.facebook.katana",
                spokenResponse = "Opening Facebook, Baby."
            )
        }
        if (text.contains("open telegram") || text.contains("টেলিগ্রাম খোলো")) {
            return ActionCommand(
                type = ActionType.OPEN_APP,
                target = "org.telegram.messenger",
                spokenResponse = "Opening Telegram, Baby."
            )
        }
        if (text.contains("open my phone settings") || text.contains("open settings") || text.contains("ফোন সেটিংস খোলো")) {
            return ActionCommand(
                type = ActionType.OPEN_SETTINGS,
                spokenResponse = "Opening your device Settings, Baby."
            )
        }
        if (text.contains("open phone dialer") || text.contains("open dialer") || text.contains("ডায়ালার খোলো")) {
            return ActionCommand(
                type = ActionType.OPEN_APP,
                target = "DIALER",
                spokenResponse = "Opening phone dialer, Baby."
            )
        }

        // 4. Calls
        val callRegex = "(?i)(?:call|phone|ডায়াল করো|ফোন করো)\\s+([a-zA-Z0-9\\s]+)".toRegex()
        val callMatch = callRegex.find(cleanRaw)
        if (callMatch != null) {
            val contactName = callMatch.groupValues[1].replace("(?i)ke".toRegex(), "").trim()
            if (contactName.isNotEmpty()) {
                return ActionCommand(
                    type = ActionType.CALL_CONTACT,
                    target = contactName,
                    spokenResponse = "Preparing to call $contactName, Baby. Please confirm."
                )
            }
        }

        // 5. Messages
        val msgRegex = "(?i)(?:send\\s+([a-zA-Z0-9]+)\\s+a\\s+message\\s+saying\\s+(.+))|(?:prepare\\s+a\\s+message\\s+for\\s+([a-zA-Z0-9]+))".toRegex()
        val msgMatch = msgRegex.find(cleanRaw)
        if (msgMatch != null) {
            val contactName = (msgMatch.groupValues[1].ifEmpty { msgMatch.groupValues[3] }).trim()
            val msgBody = msgMatch.groupValues[2].trim().ifEmpty { "Hello!" }
            return ActionCommand(
                type = ActionType.SEND_MESSAGE,
                target = contactName,
                secondaryData = msgBody,
                spokenResponse = "I have drafted a message for $contactName, Baby. Please review and confirm."
            )
        }

        // 6. Screen Automation Commands
        if (text == "go back" || text == "back" || text == "পিছনে যাও") {
            return ActionCommand(
                type = ActionType.AUTOMATION_SCREEN,
                target = "GLOBAL_BACK",
                spokenResponse = "Going back, Baby."
            )
        }
        if (text == "go home" || text == "home" || text == "হোমে যাও") {
            return ActionCommand(
                type = ActionType.AUTOMATION_SCREEN,
                target = "GLOBAL_HOME",
                spokenResponse = "Returning to Home screen, Baby."
            )
        }
        if (text.contains("scroll down") || text.contains("নিচে স্ক্রোল করো")) {
            return ActionCommand(
                type = ActionType.AUTOMATION_SCREEN,
                target = "SCROLL_DOWN",
                spokenResponse = "Scrolling down, Baby."
            )
        }
        if (text.contains("scroll up") || text.contains("উপরে স্ক্রোল করো")) {
            return ActionCommand(
                type = ActionType.AUTOMATION_SCREEN,
                target = "SCROLL_UP",
                spokenResponse = "Scrolling up, Baby."
            )
        }
        if (text.contains("show notifications") || text.contains("open notifications")) {
            return ActionCommand(
                type = ActionType.AUTOMATION_SCREEN,
                target = "GLOBAL_NOTIFICATIONS",
                spokenResponse = "Pulling down notification panel, Baby."
            )
        }

        // Fallback to conversational AI with Gemini!
        return ActionCommand(
            type = ActionType.CONVERSATION,
            query = raw
        )
    }
}
