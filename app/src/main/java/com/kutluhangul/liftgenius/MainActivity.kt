package com.kutluhangul.liftgenius

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutluhangul.liftgenius.ui.auth.SessionViewModel
import com.kutluhangul.liftgenius.ui.components.BrandWordmark
import com.kutluhangul.liftgenius.ui.components.ambientGlow
import com.kutluhangul.liftgenius.ui.main.MainScreen
import com.kutluhangul.liftgenius.ui.navigation.AuthNavHost
import com.kutluhangul.liftgenius.ui.theme.LiftGeniusTheme
import com.kutluhangul.liftgenius.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.auth.status.SessionStatus

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Transparent bars; icon contrast is set per-theme via SideEffect below.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ),
        )
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()
            LiftGeniusTheme(darkTheme = isDarkMode) {
                val view = LocalView.current
                SideEffect {
                    // Match system-bar icon contrast to the active theme.
                    WindowCompat.getInsetsController(window, view)
                        .isAppearanceLightStatusBars = !isDarkMode
                }
                LiftGeniusAppRoot()
            }
        }
    }
}

/** Top-level routing driven by Supabase auth state — no manual navigation between flows. */
@Composable
fun LiftGeniusAppRoot(sessionViewModel: SessionViewModel = hiltViewModel()) {
    val status by sessionViewModel.sessionStatus.collectAsState()
    when (status) {
        is SessionStatus.Initializing -> BrandScreen()
        is SessionStatus.Authenticated -> MainScreen()
        else -> AuthNavHost()
    }
}

/** Splash shown while the stored session is being restored. */
@Composable
fun BrandScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .ambientGlow(),
        contentAlignment = Alignment.Center,
    ) {
        BrandWordmark()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0E13)
@Composable
fun BrandScreenPreview() {
    LiftGeniusTheme {
        BrandScreen()
    }
}
