package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.VolumeOff
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AssistantStatus
import com.example.ui.components.CharacterAvatarView
import com.example.ui.components.GlassCard
import com.example.ui.components.SoundWaveIndicator
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraDeepBackground
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonPink
import com.example.ui.theme.AuraNeonPurple
import com.example.ui.theme.AuraNeonViolet
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.viewmodel.AuraScreen
import com.example.ui.viewmodel.AuraViewModel

@Composable
fun HomeScreen(
    viewModel: AuraViewModel,
    onOpenDrawer: () -> Unit,
    onOpenChat: () -> Unit
) {
    val status by viewModel.assistantStatus.collectAsState()
    val animState by viewModel.animationState.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val audioRms by viewModel.rmsDb.collectAsState()
    val statusMsg by viewModel.statusMessage.collectAsState()

    val quickCommands = listOf(
        "Baby, open YouTube",
        "Baby, search for viral songs",
        "Baby, find Free Fire gameplay",
        "Baby, open WhatsApp",
        "Baby, call Hakim",
        "Baby, open my phone settings",
        "Baby, pause the video",
        "Baby, tell me a cute joke"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AuraDeepBackground,
                        Color(0xFF0C051A),
                        Color(0xFF130728)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Section
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Three-line Hamburger Menu
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x33A855F7))
                        .border(1.dp, AuraCardBorder, CircleShape)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.size(18.dp, 2.dp).background(AuraNeonPurple, RoundedCornerShape(1.dp)))
                        Box(modifier = Modifier.size(18.dp, 2.dp).background(AuraNeonPurple, RoundedCornerShape(1.dp)))
                        Box(modifier = Modifier.size(14.dp, 2.dp).background(AuraNeonPurple, RoundedCornerShape(1.dp)))
                    }
                }

                // Center App Logo and Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .border(1.dp, AuraNeonPurple, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "YAKUB AURA AI",
                            color = AuraTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "FEMALE AI COMPANION",
                            color = AuraNeonPink,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Quick Settings / Chat Button
                IconButton(
                    onClick = onOpenChat,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x33A855F7))
                        .border(1.dp, AuraCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Open AI Chat",
                        tint = AuraNeonPink,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Quick Status Pill Notification
            AnimatedVisibility(
                visible = statusMsg != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                statusMsg?.let { msg ->
                    GlassCard(
                        modifier = Modifier
                            .padding(top = 4.dp, bottom = 4.dp)
                            .clickable { viewModel.clearStatusMessage() }
                    ) {
                        Text(
                            text = msg,
                            color = AuraNeonCyan,
                            fontSize = 11.5.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            maxLines = 2
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // Center Section: Anime Character
            CharacterAvatarView(
                modifier = Modifier
                    .size(290.dp, 330.dp),
                animationState = animState,
                audioLevel = audioRms
            )

            Spacer(modifier = Modifier.weight(0.15f))

            // Status Badge & Animated Sound-Wave
            GlassCard(
                modifier = Modifier.padding(horizontal = 8.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pulsing LED Status Dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when (status) {
                                    AssistantStatus.READY -> Color(0xFF10B981)
                                    AssistantStatus.LISTENING -> AuraNeonCyan
                                    AssistantStatus.THINKING -> AuraNeonViolet
                                    AssistantStatus.SPEAKING -> AuraNeonPink
                                    AssistantStatus.PAUSED -> Color(0xFFF59E0B)
                                }
                            )
                    )
                    Text(
                        text = status.label,
                        color = AuraTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    SoundWaveIndicator(
                        isActive = status == AssistantStatus.LISTENING || status == AssistantStatus.SPEAKING,
                        audioLevel = audioRms,
                        barCount = 7
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Voice Command Suggestion Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickCommands.forEach { prompt ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x331E0C40))
                            .border(1.dp, AuraNeonPurple.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .clickable { viewModel.handleUserInput(prompt) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = prompt,
                            color = AuraTextSecondary,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Section: Microphone & Playback Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Pause / Resume Control
                IconButton(
                    onClick = {
                        if (status == AssistantStatus.PAUSED) {
                            viewModel.toggleListening()
                        } else {
                            viewModel.pauseAssistant()
                        }
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0x331C0C38))
                        .border(1.dp, AuraCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (status == AssistantStatus.PAUSED) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Pause / Resume Assistant",
                        tint = AuraTextSecondary
                    )
                }

                // Large Glowing Microphone Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(
                            elevation = if (isListening) 20.dp else 8.dp,
                            shape = CircleShape,
                            ambientColor = AuraNeonPurple,
                            spotColor = AuraNeonPink
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = if (isListening) listOf(AuraNeonPink, AuraNeonPurple, Color(0xFF4C1D95))
                                else listOf(AuraNeonPurple, AuraNeonViolet, Color(0xFF1E0A3C))
                            )
                        )
                        .border(2.dp, if (isListening) AuraNeonPink else AuraNeonPurple, CircleShape)
                        .clickable { viewModel.toggleListening() }
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Microphone Toggle",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Stop Speaking Control
                IconButton(
                    onClick = { viewModel.stopSpeaking() },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0x331C0C38))
                        .border(1.dp, AuraCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop Voice",
                        tint = AuraTextSecondary
                    )
                }
            }
        }
    }
}
