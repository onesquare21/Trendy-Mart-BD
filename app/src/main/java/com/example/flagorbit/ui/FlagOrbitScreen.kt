package com.example.flagorbit.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.flagorbit.audio.GameAudioManager
import com.example.flagorbit.engine.FlagOrbitGameEngine
import kotlin.math.min

@Composable
fun FlagOrbitScreen(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  val audioManager = remember { GameAudioManager(context) }
  val gameEngine = remember { FlagOrbitGameEngine(audioManager) }

  val view = androidx.compose.ui.platform.LocalView.current
  DisposableEffect(view) {
    view.keepScreenOn = true
    onDispose {
      view.keepScreenOn = false
    }
  }

  var isAppActive by remember { mutableStateOf(true) }
  var renderTick by remember { mutableIntStateOf(0) }
  var lastFrameNano by remember { mutableLongStateOf(0L) }

  // Lifecycle awareness: Pause physics when backgrounded
  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      isAppActive = when (event) {
        Lifecycle.Event.ON_RESUME -> true
        Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> false
        else -> isAppActive
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
      gameEngine.release()
    }
  }

  // 60-120 FPS VSYNC-driven simulation loop
  LaunchedEffect(isAppActive) {
    lastFrameNano = 0L
    while (isAppActive) {
      withFrameNanos { nanoTime ->
        if (lastFrameNano != 0L) {
          val dt = ((nanoTime - lastFrameNano) / 1_000_000_000f).coerceIn(0.002f, 0.033f)
          gameEngine.update(dt)
          renderTick++
        }
        lastFrameNano = nanoTime
      }
    }
  }

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black)
      .testTag("flag_orbit_screen")
  ) {
    val width = constraints.maxWidth.toFloat()
    val height = constraints.maxHeight.toFloat()
    val arenaCenterX = width / 2f
    val arenaCenterY = height / 2f

    // Arena radius: centered circular arena with optimal proportions
    val availableMinDim = min(width, height)
    val arenaRadius = (availableMinDim * 0.47f).coerceAtLeast(140f)

    // Notify game engine of arena dimensions
    LaunchedEffect(arenaCenterX, arenaCenterY, arenaRadius) {
      gameEngine.setupArena(arenaCenterX, arenaCenterY, arenaRadius)
    }

    // Main Hardware-Accelerated Simulation Canvas (Strictly No Text)
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
          detectTapGestures { offset ->
            gameEngine.onUserTouch(offset.x, offset.y)
          }
        }
    ) {
      // Recomposition trigger
      val tick = renderTick

      val center = Offset(arenaCenterX, arenaCenterY)
      val isFinalFive = gameEngine.currentSurvivorCount in 2..5

      // 1. Arena Ambient Neon Rim Glow (Enhanced during Last 5 Survival Drama)
      val rimGlowColors = if (isFinalFive) {
        listOf(
          Color.Transparent,
          Color(0x2200E5FF),
          Color(0x5500D4FF),
          Color(0x25FFD700),
          Color.Transparent
        )
      } else {
        listOf(
          Color.Transparent,
          Color(0x0A00E5FF),
          Color(0x2800D4FF),
          Color.Transparent
        )
      }

      drawCircle(
        brush = Brush.radialGradient(
          colors = rimGlowColors,
          center = center,
          radius = arenaRadius * 1.06f
        ),
        radius = arenaRadius * 1.06f,
        center = center
      )

      // 2. Arena Floor (Pure cinematic dark backdrop with subtle deep gradient)
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFF04060D),
            Color(0xFF020306),
            Color(0xFF000000)
          ),
          center = center,
          radius = arenaRadius
        ),
        radius = arenaRadius,
        center = center
      )

      // Subtle cinematic drama spotlight during Last 5 battle
      if (isFinalFive) {
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0x1800E5FF),
              Color(0x08FFD700),
              Color.Transparent
            ),
            center = Offset(arenaCenterX, arenaCenterY - arenaRadius * 0.12f),
            radius = arenaRadius * 0.88f
          ),
          radius = arenaRadius,
          center = center
        )
      }

      // 3. Metallic Circular Boundary Ring with Cinematic Lighting & Soft Edge Reflections
      // Outer subtle shadow
      drawCircle(
        color = Color(0x44000000),
        radius = arenaRadius + 2.5f,
        center = center,
        style = Stroke(width = 4.5f)
      )
      // Metallic perimeter with brushed bevel lighting (heightened reflections in final five)
      val bevelColors = if (isFinalFive) {
        listOf(
          Color(0xFF64748B),
          Color(0xFFCBD5E1), // Heightened reflection
          Color(0xFF334155),
          Color(0xFFFFD700).copy(alpha = 0.5f), // Golden drama gleam
          Color(0xFF64748B)
        )
      } else {
        listOf(
          Color(0xFF475569),
          Color(0xFF94A3B8), // Top-right light reflection
          Color(0xFF334155),
          Color(0xFF64748B), // Bottom-left fill reflection
          Color(0xFF475569)
        )
      }

      drawCircle(
        brush = Brush.sweepGradient(
          colors = bevelColors,
          center = center
        ),
        radius = arenaRadius,
        center = center,
        style = Stroke(width = if (isFinalFive) 3.0f else 2.6f)
      )
      // Inner glowing neon reflection edge
      val innerNeonColor = if (isFinalFive) Color(0xAA38BDF8) else Color(0x6638BDF8)
      drawCircle(
        color = innerNeonColor,
        radius = arenaRadius - 1.4f,
        center = center,
        style = Stroke(width = 1.3f)
      )

      // 4. Draw Arena Shockwave Ripples
      for (ripple in gameEngine.particleSystem.activeRipples) {
        drawCircle(
          color = ripple.color.copy(alpha = ripple.alpha),
          radius = ripple.radius,
          center = Offset(ripple.x, ripple.y),
          style = Stroke(width = 2f)
        )
      }

      // 5. Draw Flag Balls (with 3D spherical shading, rotational spin, and neon aura)
      for (ball in gameEngine.balls) {
        FlagBallRenderer.drawBall(this, ball)
      }

      // 6. Draw Shatter & Spark Particles with Motion Trails
      for (p in gameEngine.particleSystem.activeParticles) {
        // Motion trail line
        drawLine(
          color = p.color.copy(alpha = p.alpha * 0.45f),
          start = Offset(p.prevX, p.prevY),
          end = Offset(p.x, p.y),
          strokeWidth = (p.size * 0.75f).coerceAtLeast(1.5f)
        )
        // Particle head
        drawCircle(
          color = p.color.copy(alpha = p.alpha),
          radius = p.size,
          center = Offset(p.x, p.y)
        )
      }
    }

    // Winner Announcement Celebration (Strictly the only text exception to the zero-text rule)
    val winner = gameEngine.winnerAnnouncement
    if (winner != null) {
      WinnerAnnouncementOverlay(
        winner = winner,
        progress = (gameEngine.transitionTimer / 2.0f).coerceIn(0f, 1f),
        modifier = Modifier.align(Alignment.Center)
      )
    }
  }
}
