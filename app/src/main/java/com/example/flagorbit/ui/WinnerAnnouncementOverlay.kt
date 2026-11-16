package com.example.flagorbit.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.flagorbit.model.FlagBall
import com.example.flagorbit.model.WinnerInfo
import kotlin.math.PI
import kotlin.math.sin

private val MontserratFontFamily = FontFamily(
  Font(R.font.montserrat, FontWeight.Bold)
)

/**
 * Premium cinematic winner announcement card.
 * Displayed exclusively for 2–3 seconds when a round concludes.
 */
@Composable
fun WinnerAnnouncementOverlay(
  winner: WinnerInfo,
  progress: Float,
  modifier: Modifier = Modifier
) {
  // Smooth entrance (0..0.15), sustained breath (0.15..0.85), and soft exit (0.85..1.0)
  val alpha = when {
    progress < 0.15f -> (progress / 0.15f).coerceIn(0f, 1f)
    progress > 0.85f -> ((1f - progress) / 0.15f).coerceIn(0f, 1f)
    else -> 1f
  }

  val entranceFraction = (progress / 0.20f).coerceIn(0f, 1f)
  val easedEntrance = FastOutSlowInEasing.transform(entranceFraction)
  val baseScale = 0.78f + (0.22f * easedEntrance)
  val breathScale = 1.0f + 0.02f * sin(progress * PI.toFloat() * 3f)
  val combinedScale = baseScale * breathScale

  Box(
    modifier = modifier
      .alpha(alpha)
      .scale(combinedScale)
      .padding(horizontal = 24.dp)
      .widthIn(max = 380.dp)
      .shadow(
        elevation = 32.dp,
        shape = RoundedCornerShape(28.dp),
        ambientColor = Color(0xFFFFD700),
        spotColor = Color(0xFFFFB300)
      )
      .clip(RoundedCornerShape(28.dp))
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xF00E1424),
            Color(0xF5060A12),
            Color(0xF8020408)
          )
        )
      )
      .border(
        width = 1.5.dp,
        brush = Brush.linearGradient(
          colors = listOf(
            Color(0xFFFFDF00),
            Color(0x66FFD700),
            Color(0x33FFFFFF),
            Color(0x88FFB300)
          )
        ),
        shape = RoundedCornerShape(28.dp)
      )
      .padding(horizontal = 24.dp, vertical = 24.dp)
      .testTag("winner_announcement_overlay"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // 1. Subtle Golden Category Badge
      Box(
        modifier = Modifier
          .clip(CircleShape)
          .background(Color(0x33FFD700))
          .border(0.8.dp, Color(0x88FFD700), CircleShape)
          .padding(horizontal = 14.dp, vertical = 4.dp)
      ) {
        Text(
          text = "ROUND ${winner.roundNumber} CHAMPION",
          style = TextStyle(
            fontFamily = MontserratFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            letterSpacing = 2.5.sp,
            color = Color(0xFFFFE066)
          )
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 2. Prominent 3D Winning Marble with Radiant Victory Aura
      val displayBall = remember(winner) {
        FlagBall(
          id = -1,
          country = winner.country,
          x = 0f,
          y = 0f,
          vx = 0f,
          vy = 0f,
          radius = 48f
        ).apply {
          isVictor = true
          spawnScale = 1f
          pulsePhase = (progress * 10f)
        }
      }

      Box(
        modifier = Modifier.size(width = 140.dp, height = 150.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val centerX = size.width / 2f
          val ballCenterY = size.height * 0.62f
          val ballRadius = size.width * 0.27f
          val crownCenterY = size.height * 0.20f
          val crownWidth = (ballRadius * 1.45f).coerceAtLeast(42f)

          // 1. Celebratory radiant golden aura
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(
                Color(0x77FFD700),
                Color(0x28FFB300),
                Color.Transparent
              ),
              center = Offset(centerX, ballCenterY),
              radius = size.width * 0.55f
            ),
            radius = size.width * 0.55f,
            center = Offset(centerX, ballCenterY)
          )

          // 2. Draw 3D Winning Flag Marble
          displayBall.x = centerX
          displayBall.y = ballCenterY
          displayBall.radius = ballRadius
          displayBall.pulsePhase = progress * 12f
          displayBall.isVictor = false // Render marble body with caustics and highlights

          FlagBallRenderer.drawBall(this, displayBall)

          // 3. Prominently draw Golden Champion Crown hovering above the winning ball
          val hoverOffset = (3f * sin(progress * PI.toFloat() * 6f)).toFloat()
          FlagBallRenderer.drawChampionCrown(
            drawScope = this,
            centerX = centerX,
            centerY = crownCenterY + hoverOffset,
            crownWidth = crownWidth
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Dynamic Winner Text Announcement (e.g. "🇧🇩 BANGLADESH WINS!")
      Text(
        text = winner.announcementText,
        textAlign = TextAlign.Center,
        style = TextStyle(
          fontFamily = MontserratFontFamily,
          fontWeight = FontWeight.ExtraBold,
          fontSize = 22.sp,
          letterSpacing = 1.2.sp,
          color = Color.White,
          shadow = Shadow(
            color = Color(0xCCFFD700),
            offset = Offset(0f, 3f),
            blurRadius = 14f
          )
        ),
        modifier = Modifier.testTag("winner_announcement_text")
      )
    }
  }
}
