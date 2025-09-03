package com.example.dnbn_friend.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2196F3),
    onPrimary = Color.White,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onSurface = Color(0xFF212121),
    secondary = Color(0xFF4CAF50)
)

@Composable
fun SmartphoneRecommenderTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}