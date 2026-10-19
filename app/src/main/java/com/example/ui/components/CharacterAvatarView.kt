package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CharacterAnimationState
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonPink
import com.example.ui.theme.AuraNeonPurple
import com.example.ui.theme.AuraNeonViolet
import kotlin.math.roundToInt

@Composable
fun CharacterAvatarView(
    modifier: Modifier = Modifier,
    animationState: CharacterAnimationState = CharacterAnimationState.IDLE,
    audioLevel: Float = 0f
) {
    val transition = rememberInfiniteTransition(label = "characterAnim")

    // Gentle breathing vertical float
    val breathingOffset by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    // Subtle aura rotation
    val auraRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Thinking halo spin
    val thinkingSpin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "thinkingSpin"
    )

    // Speaking mouth/voice ripple pulse
    val voiceRipple by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(380, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "voiceRipple"
    )

    // State-based dynamic scaling and glow
    val stateScale by animateFloatAsState(
        targetValue = when (animationState) {
            CharacterAnimationState.SPEAKING -> voiceRipple
            CharacterAnimationState.LISTENING -> 1.03f + (audioLevel * 0.05f)
            CharacterAnimationState.GREETING -> 1.05f
            CharacterAnimationState.THINKING -> 1.01f
            CharacterAnimationState.PAUSED -> 0.98f
            else -> 1.0f
        },
        animationSpec = tween(300),
        label = "stateScale"
    )

    val primaryAuraColor = when (animationState) {
        CharacterAnimationState.LISTENING -> AuraNeonCyan
        CharacterAnimationState.THINKING -> AuraNeonViolet
        CharacterAnimationState.SPEAKING -> AuraNeonPink
        CharacterAnimationState.GREETING -> AuraNeonPink
        CharacterAnimationState.PAUSED -> Color(0xFF6B21A8)
        else -> AuraNeonPurple
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Outer glowing cyber aura ring
        Box(
            modifier = Modifier
                .size(310.dp)
                .rotate(if (animationState == CharacterAnimationState.THINKING) thinkingSpin else auraRotation)
                .scale(stateScale)
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            primaryAuraColor.copy(alpha = 0.8f),
                            AuraNeonPink.copy(alpha = 0.3f),
                            AuraNeonPurple.copy(alpha = 0.7f),
                            Color.Transparent,
                            primaryAuraColor.copy(alpha = 0.9f)
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Middle aura ring with dashed effect
        Box(
            modifier = Modifier
                .size(285.dp)
                .rotate(-auraRotation * 0.7f)
                .border(
                    width = 1.5.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            AuraNeonViolet.copy(alpha = 0.6f),
                            AuraNeonPink.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Anime Character Card
        Box(
            modifier = Modifier
                .size(265.dp, 330.dp)
                .offset { IntOffset(0, if (animationState != CharacterAnimationState.PAUSED) breathingOffset.roundToInt() else 0) }
                .scale(stateScale)
                .clip(RoundedCornerShape(32.dp))
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            primaryAuraColor,
                            AuraNeonPurple.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .background(Color(0xFF0F071D))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_anime_companion),
                contentDescription = "Baby - YAKUB AURA AI Personal Companion",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Sci-fi holographic overlay gradient at bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color(0x66080312),
                                Color(0xDD080312)
                            )
                        )
                    )
            )

            // Companion Name Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xB3150A2E))
                    .border(1.dp, primaryAuraColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "BABY • AURA AI",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}
