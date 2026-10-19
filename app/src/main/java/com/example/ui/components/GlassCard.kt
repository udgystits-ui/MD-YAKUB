package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AuraCardBorder
import com.example.ui.theme.AuraGlassPanel

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    borderColor: Color = AuraCardBorder,
    borderWidth: Dp = 1.dp,
    backgroundColor: Color = AuraGlassPanel,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(
                BorderStroke(
                    borderWidth,
                    Brush.verticalGradient(
                        colors = listOf(
                            borderColor,
                            borderColor.copy(alpha = 0.15f)
                        )
                    )
                ),
                shape = shape
            ),
        content = content
    )
}
