package com.example.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsVoice
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraDeepBackground
import com.example.ui.theme.AuraNeonPink
import com.example.ui.theme.AuraNeonPurple
import com.example.ui.theme.AuraNeonViolet
import com.example.ui.theme.AuraSurfaceDark
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.viewmodel.AuraScreen

@Composable
fun AuraDrawerContent(
    currentScreen: AuraScreen,
    onNavigate: (AuraScreen) -> Unit,
    onOpenExternalApiKey: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        AuraDeepBackground,
                        Color(0xFF0F071D),
                        Color(0xFF140828)
                    )
                )
            )
            .verticalScroll(scrollState)
            .padding(vertical = 24.dp)
    ) {
        // App Header in Drawer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, AuraNeonPurple, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_app_logo),
                    contentDescription = "Aura Logo",
                    modifier = Modifier.size(54.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "YAKUB AURA AI",
                    color = AuraTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Baby • Anime Assistant",
                    color = AuraNeonPink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = AuraCardBorder, thickness = 0.8.dp, modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(modifier = Modifier.height(12.dp))

        // 13 Side Menu Items
        DrawerItem(
            icon = Icons.Default.Home,
            title = "1. Home",
            isSelected = currentScreen == AuraScreen.HOME,
            onClick = { onNavigate(AuraScreen.HOME); onCloseDrawer() }
        )
        DrawerItem(
            icon = Icons.Default.Chat,
            title = "2. AI Chat",
            isSelected = currentScreen == AuraScreen.AI_CHAT,
            onClick = { onNavigate(AuraScreen.AI_CHAT); onCloseDrawer() }
        )
        DrawerItem(
            icon = Icons.Default.Key,
            title = "3. Gemini API Key",
            isSelected = currentScreen == AuraScreen.GEMINI_KEY,
            onClick = { onNavigate(AuraScreen.GEMINI_KEY); onCloseDrawer() }
        )
        DrawerItem(
            icon = Icons.Default.OpenInNew,
            title = "4. Get Gemini API Key",
            isSelected = false,
            badge = "External",
            onClick = { onOpenExternalApiKey(); onCloseDrawer() }
        )
        DrawerItem(
            icon = Icons.Default.SettingsVoice,
            title = "5. Voice Settings",
            isSelected = currentScreen == AuraScreen.VOICE_SETTINGS,
            onClick = { onNavigate(AuraScreen.VOICE_SETTINGS); onCloseDrawer() }
        )
        DrawerItem(
            icon = Icons.Default.RecordVoiceOver,
            title = "6. Female Voice Selection",
            isSelected = currentScreen == AuraScreen.FEMALE_VOICE,
            onClick = { onNavigate(AuraScreen.FEMALE_VOICE); onCloseDrawer() }
        )
        DrawerItem(
            icon = Icons.Default.Language,
            title = "7. Language Settings",
            isSelected = currentScreen == AuraScreen.LANGUAGE_SETTINGS,
            badge = "BN / EN",
            onClick = { onNavigate(AuraScreen.LANGUAGE_SETTINGS); onCloseDrawer() }
        )
        DrawerItem(
            icon = Icons.Default.Shield,
            title = "8. App Permissions",
            isSelected = currentScreen == AuraScreen.PERMISSIONS,
            onClick = { onNavigate(AuraScreen.PERMISSIONS); onCloseDrawer() }
        )
        DrawerItem(
            icon = Icons.Default.Accessibility,
            title = "9. Accessibility Setup",
            isSelected = currentScreen == AuraScreen.ACCESSIBILITY_SETUP,
            onClick = { onNavigate(AuraScreen.ACCESSIBILITY_SETUP); onCloseDrawer() }
        )
        DrawerItem(
            icon = Icons.Default.Headphones,
            title = "10. Background Mode",
            isSelected = currentScreen == AuraScreen.BACKGROUND_MODE,
            onClick = { onNavigate(AuraScreen.BACKGROUND_MODE); onCloseDrawer() }
        )
        DrawerItem(
            icon = Icons.Default.Animation,
            title = "11. Animation Settings",
            isSelected = currentScreen == AuraScreen.ANIMATION_SETTINGS,
            onClick = { onNavigate(AuraScreen.ANIMATION_SETTINGS); onCloseDrawer() }
        )
        DrawerItem(
            icon = Icons.Default.Security,
            title = "12. Privacy and Security",
            isSelected = currentScreen == AuraScreen.PRIVACY,
            onClick = { onNavigate(AuraScreen.PRIVACY); onCloseDrawer() }
        )
        DrawerItem(
            icon = Icons.Default.Info,
            title = "13. About YAKUB AURA AI",
            isSelected = currentScreen == AuraScreen.ABOUT,
            onClick = { onNavigate(AuraScreen.ABOUT); onCloseDrawer() }
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    badge: String? = null,
    onClick: () -> Unit
) {
    val bgModifier = if (isSelected) {
        Modifier
            .background(
                Brush.horizontalGradient(
                    listOf(
                        AuraNeonPurple.copy(alpha = 0.25f),
                        AuraNeonViolet.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .border(1.dp, AuraNeonPurple.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
    } else {
        Modifier
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(bgModifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) AuraNeonPink else AuraNeonPurple,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            color = if (isSelected) AuraTextPrimary else AuraTextSecondary,
            fontSize = 13.5.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AuraNeonPurple.copy(alpha = 0.2f))
                    .border(0.5.dp, AuraNeonPurple.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    color = AuraNeonPink,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
