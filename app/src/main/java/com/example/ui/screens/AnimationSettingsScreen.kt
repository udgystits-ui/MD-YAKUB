package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Check
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
import com.example.ui.components.GlassCard
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraDeepBackground
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonPink
import com.example.ui.theme.AuraNeonPurple
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.viewmodel.AuraViewModel

@Composable
fun AnimationSettingsScreen(
    viewModel: AuraViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var selectedPreset by remember { mutableStateOf(viewModel.prefs.animationQuality) }

    val presets = listOf(
        Triple("High", "Ultra 3D Neon Aura", "Full character breathing, voice wave ripple sync, rotating cyber rings, and glowing particle energy."),
        Triple("Balanced", "Smooth Animation", "Character breathing and voice ripple without heavy ring rotation. Balanced performance."),
        Triple("Battery Saver", "Static Companion", "Fixed character rendering with minimal battery consumption for low-end devices.")
    )

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
                        text = "Animation Settings",
                        color = AuraTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Visual fidelity & performance presets",
                        color = AuraNeonPink,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                presets.forEach { (name, label, desc) ->
                    val isSelected = selectedPreset == name
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0x44A855F7) else Color(0xB3150A2E))
                            .border(
                                1.dp,
                                if (isSelected) AuraNeonPurple else AuraCardBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selectedPreset = name
                                viewModel.prefs.animationQuality = name
                                viewModel.setStatusMessage("Animation profile: $name")
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Animation,
                                contentDescription = null,
                                tint = if (isSelected) AuraNeonPink else AuraNeonPurple
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = label,
                                    color = AuraTextPrimary,
                                    fontSize = 14.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                )
                                Text(
                                    text = desc,
                                    color = AuraTextMuted,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = AuraNeonPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
