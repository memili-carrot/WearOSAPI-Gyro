package com.example.wearosgyro.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.Typography

private val WearColorPalette = Colors(
    primary = androidx.compose.ui.graphics.Color(0xFF1EB980),
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    background = androidx.compose.ui.graphics.Color.Black,
    onBackground = androidx.compose.ui.graphics.Color.White
)

@Composable
fun WearOSGyroTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = WearColorPalette,
        typography = Typography(),
        content = content
    )
}