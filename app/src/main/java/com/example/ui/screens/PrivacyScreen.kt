package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.GlassCard
import com.example.ui.theme.AuraDeepBackground
import com.example.ui.theme.AuraError
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonPink
import com.example.ui.theme.AuraNeonPurple
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.viewmodel.AuraViewModel

@Composable
fun PrivacyScreen(
    viewModel: AuraViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    var showClearHistoryConfirm by remember { mutableStateOf(false) }

    if (showClearHistoryConfirm) {
        ConfirmationDialog(
            title = "Delete Local History",
            message = "All stored conversations with Baby will be permanently removed from your device storage.",
            confirmText = "Delete Now",
            confirmColor = AuraError,
            onConfirm = {
                viewModel.clearHistory()
                showClearHistoryConfirm = false
            },
            onDismiss = { showClearHistoryConfirm = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(AuraDeepBackground, Color(0xFF0D061A), Color(0xFF130826))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x33A855F7))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AuraTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Privacy and Security",
                        color = AuraTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Data protection & on-device disclosures",
                        color = AuraNeonPink,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Transparency Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = AuraNeonPink)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Microphone Transparency",
                            color = AuraTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• The microphone is ONLY activated when you tap the microphone button or explicitly start a background session.\n" +
                                "• No continuous covert listening or stealth audio uploads.\n" +
                                "• Audio input is converted to text via Google Speech recognition, and only text is transmitted to Gemini API.",
                        color = AuraTextSecondary,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = AuraNeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "API Key & Storage Safeguards",
                            color = AuraTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Your custom Gemini API key is kept inside app-private SharedPreferences.\n" +
                                "• It is never shared with third parties or printed in Android system logs.\n" +
                                "• You can wipe the key anytime in the Gemini API Key menu.",
                        color = AuraTextSecondary,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Wipe History Button
            Button(
                onClick = { showClearHistoryConfirm = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x33EF4444)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, tint = AuraError)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear All Local Conversation History", color = AuraError, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
