package com.example.usdconverter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2196F3),       // Biru terang
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC5),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun USDConverterTheme(
    darkTheme: Boolean = true, // Pakai dark mode default
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme || isSystemInDarkTheme()) {
        DarkColorScheme
    } else {
        DarkColorScheme // Untuk project ini, dark saja
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}
