package com.example.aplicacionbiscotti.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val MarronBiscotti = Color(0xFF6A442A)
val BeigeBiscotti = Color(0xFFE7D7C1)
val MarronOscuro = Color(0xFF3E2723)

private val DarkColorScheme = darkColorScheme(
    primary = MarronBiscotti,
    secondary = BeigeBiscotti,
    tertiary = Color(0xFFD7CCC8)
)

private val LightColorScheme = lightColorScheme(
    primary = MarronBiscotti,
    onPrimary = Color.White,
    
    secondary = MarronOscuro,
    onSecondary = Color.White,

    background = BeigeBiscotti,
    onBackground = MarronOscuro,

    surface = BeigeBiscotti,
    onSurface = MarronOscuro
)

@Composable
fun TemaBiscotti(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}