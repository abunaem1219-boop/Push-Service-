package com.taqwanet.app.core.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// তাকওয়ানেট সিগনেচার ডার্ক থিম (প্রধান ভিজ্যুয়াল ভিত্তি)
private val DarkColorScheme = darkColorScheme(
    primary = MintBright,
    onPrimary = EmeraldDeepDark,
    primaryContainer = MintContainer,
    onPrimaryContainer = MintSoft,
    secondary = AntiqueGold,
    onSecondary = EmeraldDeepDark,
    secondaryContainer = EmeraldCardDark,
    onSecondaryContainer = SoftGold,
    background = EmeraldDeepDark,
    onBackground = DarkTextPrimary,
    surface = EmeraldSurfaceDark,
    onSurface = DarkTextPrimary,
    surfaceVariant = EmeraldCardDark,
    onSurfaceVariant = DarkTextSecondary,
    error = ProhibitedRed,
    onError = DarkTextPrimary,
    errorContainer = ProhibitedRedContainer,
    onErrorContainer = ProhibitedRedText
)

// লাইট থিম (দিনের বেলার বিকল্প)
private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = IvorySurface,
    primaryContainer = MintSoft,
    onPrimaryContainer = EmeraldDeepDark,
    secondary = AntiqueGold,
    onSecondary = EmeraldDeepDark,
    secondaryContainer = IvoryCard,
    onSecondaryContainer = IvoryTextPrimary,
    background = IvoryBackground,
    onBackground = IvoryTextPrimary,
    surface = IvorySurface,
    onSurface = IvoryTextPrimary,
    surfaceVariant = IvoryCard,
    onSurfaceVariant = IvoryTextSecondary,
    error = ProhibitedRed,
    onError = IvorySurface,
    errorContainer = ProhibitedRedContainer,
    onErrorContainer = ProhibitedRedText
)

@Composable
fun TaqwaNetTheme(
    darkTheme: Boolean = true, // ডিফল্টভাবে প্রিমিয়াম ডার্ক-এমারেল্ড লুক
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = colorScheme.background.toArgb()
                it.navigationBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(it, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TaqwaTypography,
        content = content
    )
}