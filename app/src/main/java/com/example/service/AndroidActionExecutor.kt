package com.example.service

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.AudioManager
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Settings
import android.view.KeyEvent
import java.net.URLEncoder

data class ContactMatch(
    val id: String,
    val name: String,
    val phoneNumber: String
)

class AndroidActionExecutor(private val context: Context) {

    fun isAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun launchApp(packageName: String): Boolean {
        if (packageName == "DIALER") {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            return try {
                context.startActivity(intent)
                true
            } catch (_: Exception) {
                false
            }
        }

        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        return if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            true
        } else {
            false
        }
    }

    fun searchYouTube(query: String): Boolean {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val appIntent = Intent(Intent.ACTION_SEARCH).apply {
            setPackage("com.google.android.youtube")
            putExtra("query", query)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(appIntent)
            true
        } catch (_: Exception) {
            // Web fallback
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$encodedQuery")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(webIntent)
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    fun controlMedia(action: String): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return false
        val keyCode = when (action) {
            "PAUSE", "RESUME", "TOGGLE" -> KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
            "NEXT" -> KeyEvent.KEYCODE_MEDIA_NEXT
            "PREV" -> KeyEvent.KEYCODE_MEDIA_PREVIOUS
            else -> KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
        }
        return try {
            audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openSettings(): Boolean {
        val intent = Intent(Settings.ACTION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun findContacts(nameQuery: String): List<ContactMatch> {
        val list = mutableListOf<ContactMatch>()
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$nameQuery%")

        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            cursor?.let {
                val idIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (it.moveToNext()) {
                    val id = if (idIdx >= 0) it.getString(idIdx) else ""
                    val name = if (nameIdx >= 0) it.getString(nameIdx) else ""
                    val number = if (numberIdx >= 0) it.getString(numberIdx) else ""
                    if (name.isNotEmpty() && number.isNotEmpty()) {
                        list.add(ContactMatch(id, name, number))
                    }
                }
            }
        } catch (_: SecurityException) {
            // Permission not granted
        } catch (_: Exception) {
            // Error reading contacts
        } finally {
            cursor?.close()
        }
        return list
    }

    fun dialPhoneNumber(phoneNumber: String): Boolean {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phoneNumber.trim()}")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun composeSms(phoneNumber: String, messageText: String): Boolean {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${phoneNumber.trim()}")).apply {
            putExtra("sms_body", messageText)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }
}
