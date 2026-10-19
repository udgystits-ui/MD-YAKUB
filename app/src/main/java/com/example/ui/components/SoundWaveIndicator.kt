package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AuraNeonPink
import com.example.ui.theme.AuraNeonPurple
import com.example.ui.theme.AuraNeonViolet

@Composable
fun SoundWaveIndicator(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    audioLevel: Float = 0f,
    barCount: Int = 7,
    maxHeight: Dp = 28.dp,
    minHeight: Dp = 6.dp
) {
    val transition = rememberInfiniteTransition(label = "waveTransition")

    val anim1 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "anim1"
    )
    val anim2 by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "anim2"
    )
    val anim3 by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "anim3"
    )

    val waveGradient = Brush.verticalGradient(
        colors = listOf(AuraNeonPink, AuraNeonPurple, AuraNeonViolet)
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val animWeights = listOf(anim1, anim2, anim3, anim1, anim3, anim2, anim1)
        for (i in 0 until barCount) {
            val weight = if (isActive) {
                val baseAnim = animWeights[i % animWeights.size]
                (baseAnim * 0.6f + audioLevel * 0.4f).coerceIn(0.2f, 1f)
            } else {
                0.15f
            }
            val currentHeight = minHeight + (maxHeight - minHeight) * weight

            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(currentHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isActive) waveGradient else Brush.verticalGradient(listOf(AuraNeonPurple.copy(alpha = 0.3f), AuraNeonViolet.copy(alpha = 0.2f))))
            )
        }
    }
}
