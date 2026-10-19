package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.GlassCard
import com.example.ui.navigation.AuraDrawerContent
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AccessibilityScreen
import com.example.ui.screens.AiChatScreen
import com.example.ui.screens.AnimationSettingsScreen
import com.example.ui.screens.ApiKeyScreen
import com.example.ui.screens.BackgroundModeScreen
import com.example.ui.screens.FemaleVoiceScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LanguageSettingsScreen
import com.example.ui.screens.PermissionsScreen
import com.example.ui.screens.PrivacyScreen
import com.example.ui.screens.VoiceSettingsScreen
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraDeepBackground
import com.example.ui.theme.AuraNeonPink
import com.example.ui.theme.AuraNeonPurple
import com.example.ui.theme.AuraTextMuted
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AuraScreen
import com.example.ui.viewmodel.AuraViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: AuraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AuraMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AuraMainApp(viewModel: AuraViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val pendingAction by viewModel.pendingAction.collectAsState()

    var showOnboarding by remember { mutableStateOf(!viewModel.prefs.onboardingCompleted) }

    // Pending Action Confirmation Dialog (Calls, SMS, etc.)
    pendingAction?.let { action ->
        ConfirmationDialog(
            title = action.title,
            message = action.description,
            confirmText = "Proceed",
            confirmColor = AuraNeonPurple,
            onConfirm = action.onConfirm,
            onDismiss = { viewModel.dismissPendingAction() }
        )
    }

    // First-Time Setup Onboarding Dialog
    if (showOnboarding) {
        Dialog(onDismissRequest = { /* force action */ }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AuraDeepBackground)
                    .border(1.5.dp, AuraCardBorder, RoundedCornerShape(24.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, AuraNeonPurple, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(64.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Welcome to YAKUB AURA AI",
                    color = AuraTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Meet Baby, your female anime AI companion",
                    color = AuraNeonPink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "• Talk to Baby in Bengali or English by tapping the glowing microphone.\n" +
                            "• Open YouTube, search videos, control media, launch WhatsApp, and make calls.\n" +
                            "• Set your Gemini API key in the side menu for intelligent conversation.\n" +
                            "• Hands-free screen tasks available via Accessibility service.",
                    color = AuraTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        viewModel.prefs.onboardingCompleted = true
                        showOnboarding = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraNeonPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Get Started, Baby! ✨", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AuraDrawerContent(
                currentScreen = currentScreen,
                onNavigate = { screen ->
                    viewModel.navigateTo(screen)
                },
                onOpenExternalApiKey = {
                    viewModel.openAiStudioKeyPage()
                },
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    AuraScreen.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                            onOpenChat = { viewModel.navigateTo(AuraScreen.AI_CHAT) }
                        )
                    }

                    AuraScreen.AI_CHAT -> {
                        AiChatScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo(AuraScreen.HOME) }
                        )
                    }

                    AuraScreen.GEMINI_KEY -> {
                        ApiKeyScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo(AuraScreen.HOME) }
                        )
                    }

                    AuraScreen.VOICE_SETTINGS -> {
                        VoiceSettingsScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo(AuraScreen.HOME) }
                        )
                    }

                    AuraScreen.FEMALE_VOICE -> {
                        FemaleVoiceScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo(AuraScreen.HOME) }
                        )
                    }

                    AuraScreen.LANGUAGE_SETTINGS -> {
                        LanguageSettingsScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo(AuraScreen.HOME) }
                        )
                    }

                    AuraScreen.PERMISSIONS -> {
                        PermissionsScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo(AuraScreen.HOME) }
                        )
                    }

                    AuraScreen.ACCESSIBILITY_SETUP -> {
                        AccessibilityScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo(AuraScreen.HOME) }
                        )
                    }

                    AuraScreen.BACKGROUND_MODE -> {
                        BackgroundModeScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo(AuraScreen.HOME) }
                        )
                    }

                    AuraScreen.ANIMATION_SETTINGS -> {
                        AnimationSettingsScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo(AuraScreen.HOME) }
                        )
                    }

                    AuraScreen.PRIVACY -> {
                        PrivacyScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo(AuraScreen.HOME) }
                        )
                    }

                    AuraScreen.ABOUT -> {
                        AboutScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo(AuraScreen.HOME) }
                        )
                    }
                }
            }
        }
    }
}
