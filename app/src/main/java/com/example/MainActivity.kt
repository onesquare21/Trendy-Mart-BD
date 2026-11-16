package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.flagorbit.ui.FlagOrbitScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    volumeControlStream = android.media.AudioManager.STREAM_MUSIC
    enableEdgeToEdge()

    // Keep screen awake while app is active
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    // Fullscreen immersive mode: hide status bar and navigation bar
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    insetsController.hide(WindowInsetsCompat.Type.systemBars())

    setContent {
      MyApplicationTheme(darkTheme = true) {
        FlagOrbitScreen(modifier = Modifier.fillMaxSize())
      }
    }
  }

  override fun onResume() {
    super.onResume()
    // Re-activate keep screen awake when entering foreground
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    // Re-hide system bars
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    insetsController.hide(WindowInsetsCompat.Type.systemBars())
  }

  override fun onPause() {
    super.onPause()
    // Release keep screen awake when leaving foreground so normal device timeout can resume
    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (hasFocus) {
      window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      val insetsController = WindowCompat.getInsetsController(window, window.decorView)
      insetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
  }
}
