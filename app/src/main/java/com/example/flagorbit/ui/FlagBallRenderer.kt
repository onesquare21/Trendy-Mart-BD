package com.example.flagorbit.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotateRad
import androidx.compose.ui.graphics.drawscope.withTransform
import com.example.flagorbit.model.CountryFlag
import com.example.flagorbit.model.FlagBall
import com.example.flagorbit.model.FlagStyle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object FlagBallRenderer {

  fun drawBall(
    drawScope: DrawScope,
    ball: FlagBall
  ) {
    if (ball.isEliminated || ball.radius <= 0f) return

    val scale = ball.spawnScale.coerceIn(0.01f, 1.0f)
    val r = ball.radius * scale
    val cx = ball.x
    val cy = ball.y

    val flag = ball.country

    // 1. Target Elimination Aura / Victory Glow ONLY (No permanent outer glow on regular balls)
    if (ball.isEligibleForElimination) {
      val glowColor = Color(0xFFFF2A6D)
      val glowAlpha = (0.70f + 0.30f * sin(ball.pulsePhase)).coerceIn(0.4f, 1.0f)
      val glowRadius = r * 1.55f
      drawScope.drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            glowColor.copy(alpha = glowAlpha),
            glowColor.copy(alpha = glowAlpha * 0.35f),
            Color.Transparent
          ),
          center = Offset(cx, cy),
          radius = glowRadius
        ),
        radius = glowRadius,
        center = Offset(cx, cy)
      )
    } else if (ball.isVictor) {
      val glowColor = Color(0xFFFFD700)
      val glowAlpha = (0.85f + 0.15f * sin(ball.pulsePhase)).coerceIn(0.7f, 1.0f)
      val glowRadius = r * 1.85f
      drawScope.drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            glowColor.copy(alpha = glowAlpha),
            glowColor.copy(alpha = glowAlpha * 0.4f),
            Color.Transparent
          ),
          center = Offset(cx, cy),
          radius = glowRadius
        ),
        radius = glowRadius,
        center = Offset(cx, cy)
      )
    }

    // Subtle natural contact shadow beneath the glass marble
    drawScope.drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0x33000000),
          Color.Transparent
        ),
        center = Offset(cx, cy + r * 0.12f),
        radius = r * 1.25f
      ),
      radius = r * 1.25f,
      center = Offset(cx, cy + r * 0.12f)
    )

    // 2. Subtle Physical Impact Compression & Elastic Rebound
    val centerOffset = Offset(cx, cy)
    val hasSquash = ball.squashAmount > 0.001f
    val normalAngleRad = kotlin.math.atan2(ball.squashNormalY, ball.squashNormalX)
    val normalAngleDeg = (normalAngleRad * 180f / PI.toFloat())
    val scaleAlongNormal = (1.0f - ball.squashAmount * 0.065f).coerceIn(0.92f, 1.0f)
    val scalePerpendicular = (1.0f + ball.squashAmount * 0.065f).coerceIn(1.0f, 1.08f)

    drawScope.withTransform({
      if (hasSquash) {
        rotate(degrees = normalAngleDeg, pivot = centerOffset)
        scale(scaleX = scaleAlongNormal, scaleY = scalePerpendicular, pivot = centerOffset)
        rotate(degrees = -normalAngleDeg, pivot = centerOffset)
      }
    }) {
      val ballPath = Path().apply {
        addOval(Rect(cx - r, cy - r, cx + r, cy + r))
      }

      drawScope.clipPath(ballPath) {
        // A. Rotating flag artwork inside the marble
        withTransform({
          rotateRad(radians = ball.angle, pivot = Offset(cx, cy))
        }) {
          drawFlagArtwork(this, cx, cy, r, flag)
        }

        // B. Spherical Curvature & Volume (Natural 3D depth darkening towards perimeter)
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color.Transparent,
              Color.Transparent,
              Color(0x18000000),
              Color(0x70000000),
              Color(0xAA000000)
            ),
            center = Offset(cx + r * 0.18f, cy + r * 0.18f),
            radius = r * 1.04f
          ),
          radius = r,
          center = Offset(cx, cy)
        )

        // C. Glass Internal Caustic Glow (Light refracting through glass to bottom-right)
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0x44FFFFFF),
              Color(0x18FFFFFF),
              Color.Transparent
            ),
            center = Offset(cx + r * 0.32f, cy + r * 0.32f),
            radius = r * 0.65f
          ),
          radius = r * 0.65f,
          center = Offset(cx + r * 0.32f, cy + r * 0.32f)
        )

        // D. Translucent Glass Vitreous Sheen
        drawCircle(
          brush = Brush.linearGradient(
            colors = listOf(
              Color(0x28FFFFFF),
              Color.Transparent,
              Color(0x15FFFFFF)
            ),
            start = Offset(cx - r, cy - r),
            end = Offset(cx + r, cy + r)
          ),
          radius = r,
          center = Offset(cx, cy)
        )

        // E. Secondary Soft Bounce Highlight (Reflecting arena floor on bottom-right)
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0x55FFFFFF),
              Color(0x20FFFFFF),
              Color.Transparent
            ),
            center = Offset(cx + r * 0.30f, cy + r * 0.30f),
            radius = r * 0.48f
          ),
          radius = r * 0.48f,
          center = Offset(cx + r * 0.30f, cy + r * 0.30f)
        )

        // F. Primary Crisp 3D Specular Curved Highlight (Top-left light reflection)
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0xF0FFFFFF),
              Color(0x88FFFFFF),
              Color(0x22FFFFFF),
              Color.Transparent
            ),
            center = Offset(cx - r * 0.34f, cy - r * 0.34f),
            radius = r * 0.52f
          ),
          radius = r * 0.52f,
          center = Offset(cx - r * 0.34f, cy - r * 0.34f)
        )

        // G. Pinpoint Specular Direct Glint
        drawCircle(
          color = Color(0xFFFFFFFF),
          radius = (r * 0.08f).coerceAtLeast(1.5f),
          center = Offset(cx - r * 0.36f, cy - r * 0.36f)
        )

        // H. Flash effect on elimination or spawn
        if (ball.flashIntensity > 0f) {
          drawCircle(
            color = Color.White.copy(alpha = ball.flashIntensity.coerceIn(0f, 1f)),
            radius = r,
            center = Offset(cx, cy)
          )
        }
      }

      // 3. Realistic Glass Fresnel Rim (Clean curved glass perimeter reflection)
      val rimColor = if (ball.isEligibleForElimination) {
        Color(0xFFFF5588)
      } else {
        Color(0x44FFFFFF)
      }

      drawScope.drawCircle(
        brush = Brush.linearGradient(
          colors = listOf(
            Color(0x99FFFFFF),
            rimColor,
            Color(0x33FFFFFF),
            rimColor
          ),
          start = Offset(cx - r, cy - r),
          end = Offset(cx + r, cy + r)
        ),
        radius = r,
        center = Offset(cx, cy),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.4f)
      )
    }

    // 4. Real-Time Glass Specular Reflection Highlight at Collision Contact Point
    if (ball.contactGlintIntensity > 0.02f) {
      val glintAlpha = ball.contactGlintIntensity.coerceIn(0f, 1f)
      val gx = ball.contactGlintX
      val gy = ball.contactGlintY
      val glintRadius = (r * 0.28f).coerceIn(3.5f, 11f)

      // Delicate soft contact refraction glow
      drawScope.drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color.White.copy(alpha = glintAlpha * 0.92f),
            Color(0x88E0F2FE).copy(alpha = glintAlpha * 0.45f),
            Color.Transparent
          ),
          center = Offset(gx, gy),
          radius = glintRadius * 1.5f
        ),
        radius = glintRadius * 1.5f,
        center = Offset(gx, gy)
      )

      // Crisp diamond specular point
      drawScope.drawCircle(
        color = Color.White.copy(alpha = glintAlpha),
        radius = glintRadius * 0.40f,
        center = Offset(gx, gy)
      )
    }

    // 6. Golden Champion Crown displayed above winning ball
    if (ball.isVictor) {
      val floatOffset = (4f + 3f * sin(ball.pulsePhase)).coerceAtLeast(0f)
      val crownWidth = (r * 1.35f).coerceIn(28f, 72f)
      val crownY = cy - r - crownWidth * 0.42f - floatOffset
      drawChampionCrown(drawScope, cx, crownY, crownWidth)
    }
  }

  fun drawChampionCrown(
    drawScope: DrawScope,
    centerX: Float,
    centerY: Float,
    crownWidth: Float
  ) {
    val w = crownWidth
    val h = w * 0.62f
    val left = centerX - w / 2f
    val top = centerY - h / 2f
    val bottom = centerY + h / 2f

    // 1. Radiant Golden Crown Ambient Glow
    drawScope.drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0x99FFD700),
          Color(0x33FFB300),
          Color.Transparent
        ),
        center = Offset(centerX, centerY),
        radius = w * 0.85f
      ),
      radius = w * 0.85f,
      center = Offset(centerX, centerY)
    )

    // 2. Imperial 5-Point Crown Body Path
    val crownPath = Path().apply {
      moveTo(left, bottom - h * 0.18f)
      lineTo(left + w * 0.04f, top + h * 0.28f)
      lineTo(left + w * 0.28f, top + h * 0.52f)
      lineTo(left + w * 0.38f, top + h * 0.15f)
      lineTo(left + w * 0.46f, top + h * 0.45f)
      lineTo(centerX, top)
      lineTo(left + w * 0.54f, top + h * 0.45f)
      lineTo(left + w * 0.62f, top + h * 0.15f)
      lineTo(left + w * 0.72f, top + h * 0.52f)
      lineTo(left + w * 0.96f, top + h * 0.28f)
      lineTo(left + w, bottom - h * 0.18f)
      quadraticTo(centerX, bottom, left, bottom - h * 0.18f)
      close()
    }

    // Draw Crown Body Gradient (Lustrous 24K Gold)
    drawScope.drawPath(
      path = crownPath,
      brush = Brush.linearGradient(
        colors = listOf(
          Color(0xFFFFEE77),
          Color(0xFFFFD700),
          Color(0xFFFFB300),
          Color(0xFFFF8F00),
          Color(0xFFFFEE77)
        ),
        start = Offset(left, top),
        end = Offset(left + w, bottom)
      )
    )

    // 3. Gold Trim Highlight
    drawScope.drawPath(
      path = crownPath,
      color = Color(0xFFFFF8E7),
      style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.4f)
    )

    // 4. Base Velvet & Gold Circlet Band
    val bandHeight = h * 0.18f
    val bandTop = bottom - bandHeight
    drawScope.drawRoundRect(
      brush = Brush.horizontalGradient(
        colors = listOf(
          Color(0xFFFFB300),
          Color(0xFFFFEE77),
          Color(0xFFFFD700),
          Color(0xFFFF8F00)
        )
      ),
      topLeft = Offset(left + w * 0.05f, bandTop),
      size = Size(w * 0.90f, bandHeight),
      cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
    )

    // 5. Crown Jewels on the Base Band (Center Ruby + Flanking Jewels)
    val gemRadius = (bandHeight * 0.32f).coerceAtLeast(1.8f)
    val gemY = bandTop + bandHeight * 0.5f

    drawScope.drawCircle(
      color = Color(0xFFE50914),
      radius = gemRadius * 1.2f,
      center = Offset(centerX, gemY)
    )
    drawScope.drawCircle(
      color = Color.White,
      radius = gemRadius * 0.4f,
      center = Offset(centerX - gemRadius * 0.3f, gemY - gemRadius * 0.3f)
    )

    val gemOffset = w * 0.24f
    drawScope.drawCircle(
      color = Color(0xFF00E676),
      radius = gemRadius,
      center = Offset(centerX - gemOffset, gemY)
    )
    drawScope.drawCircle(
      color = Color(0xFF2979FF),
      radius = gemRadius,
      center = Offset(centerX + gemOffset, gemY)
    )

    // 6. Brilliant Pearls on Crown Peaks
    val pearlRadius = (w * 0.032f).coerceAtLeast(1.8f)
    val peaks = listOf(
      Offset(left + w * 0.04f, top + h * 0.28f),
      Offset(left + w * 0.38f, top + h * 0.15f),
      Offset(centerX, top),
      Offset(left + w * 0.62f, top + h * 0.15f),
      Offset(left + w * 0.96f, top + h * 0.28f)
    )
    for (peak in peaks) {
      drawScope.drawCircle(
        color = Color(0xFFFFF9E6),
        radius = pearlRadius,
        center = peak
      )
      drawScope.drawCircle(
        color = Color.White,
        radius = pearlRadius * 0.45f,
        center = Offset(peak.x - pearlRadius * 0.25f, peak.y - pearlRadius * 0.25f)
      )
    }
  }

  private fun drawFlagArtwork(
    drawScope: DrawScope,
    cx: Float,
    cy: Float,
    r: Float,
    flag: CountryFlag
  ) {
    val left = cx - r
    val top = cy - r
    val diameter = r * 2f
    val colors = flag.colors

    when (flag.style) {
      FlagStyle.HORIZONTAL_2 -> {
        val c1 = colors.getOrElse(0) { Color.White }
        val c2 = colors.getOrElse(1) { Color.Red }
        drawScope.drawRect(c1, topLeft = Offset(left, top), size = Size(diameter, r))
        drawScope.drawRect(c2, topLeft = Offset(left, cy), size = Size(diameter, r))
      }

      FlagStyle.HORIZONTAL_3 -> {
        val h = diameter / 3f
        drawScope.drawRect(colors.getOrElse(0) { Color.Black }, topLeft = Offset(left, top), size = Size(diameter, h))
        drawScope.drawRect(colors.getOrElse(1) { Color.Red }, topLeft = Offset(left, top + h), size = Size(diameter, h))
        drawScope.drawRect(colors.getOrElse(2) { Color.Yellow }, topLeft = Offset(left, top + h * 2), size = Size(diameter, h))
        drawOptionalSymbol(drawScope, cx, cy, r, flag)
      }

      FlagStyle.HORIZONTAL_3_DIFF -> {
        // e.g. Colombia, Spain: 50% top, 25% middle, 25% bottom
        val h1 = diameter * 0.5f
        val h2 = diameter * 0.25f
        drawScope.drawRect(colors.getOrElse(0) { Color.Yellow }, topLeft = Offset(left, top), size = Size(diameter, h1))
        drawScope.drawRect(colors.getOrElse(1) { Color.Blue }, topLeft = Offset(left, top + h1), size = Size(diameter, h2))
        drawScope.drawRect(colors.getOrElse(2) { Color.Red }, topLeft = Offset(left, top + h1 + h2), size = Size(diameter, h2))
        drawOptionalSymbol(drawScope, cx, cy, r, flag)
      }

      FlagStyle.VERTICAL_2 -> {
        val w1 = diameter * 0.4f
        val w2 = diameter * 0.6f
        drawScope.drawRect(colors.getOrElse(0) { Color.Green }, topLeft = Offset(left, top), size = Size(w1, diameter))
        drawScope.drawRect(colors.getOrElse(1) { Color.Red }, topLeft = Offset(left + w1, top), size = Size(w2, diameter))
        drawOptionalSymbol(drawScope, cx, cy, r, flag)
      }

      FlagStyle.VERTICAL_3 -> {
        val w = diameter / 3f
        drawScope.drawRect(colors.getOrElse(0) { Color.Blue }, topLeft = Offset(left, top), size = Size(w, diameter))
        drawScope.drawRect(colors.getOrElse(1) { Color.White }, topLeft = Offset(left + w, top), size = Size(w, diameter))
        drawScope.drawRect(colors.getOrElse(2) { Color.Red }, topLeft = Offset(left + w * 2, top), size = Size(w, diameter))
        drawOptionalSymbol(drawScope, cx, cy, r, flag)
      }

      FlagStyle.NORDIC_CROSS -> {
        val bg = colors.getOrElse(0) { Color.Blue }
        val cross = colors.getOrElse(1) { Color.Yellow }
        val innerCross = colors.getOrNull(2)

        drawScope.drawRect(bg, topLeft = Offset(left, top), size = Size(diameter, diameter))

        val crossThickness = r * 0.35f
        val crossX = left + diameter * 0.38f - crossThickness / 2f
        val crossY = cy - crossThickness / 2f

        if (innerCross != null) {
          // Double cross (e.g. Norway, Iceland)
          val outerThickness = crossThickness * 1.5f
          val outerX = left + diameter * 0.38f - outerThickness / 2f
          val outerY = cy - outerThickness / 2f
          drawScope.drawRect(cross, topLeft = Offset(outerX, top), size = Size(outerThickness, diameter))
          drawScope.drawRect(cross, topLeft = Offset(left, outerY), size = Size(diameter, outerThickness))

          drawScope.drawRect(innerCross, topLeft = Offset(crossX, top), size = Size(crossThickness, diameter))
          drawScope.drawRect(innerCross, topLeft = Offset(left, crossY), size = Size(diameter, crossThickness))
        } else {
          drawScope.drawRect(cross, topLeft = Offset(crossX, top), size = Size(crossThickness, diameter))
          drawScope.drawRect(cross, topLeft = Offset(left, crossY), size = Size(diameter, crossThickness))
        }
      }

      FlagStyle.GREEK_CROSS -> {
        val bg = colors.getOrElse(0) { Color.Red }
        val crossColor = colors.getOrElse(1) { Color.White }
        drawScope.drawRect(bg, topLeft = Offset(left, top), size = Size(diameter, diameter))

        val armLen = r * 0.95f
        val armWidth = r * 0.32f
        // Vertical arm
        drawScope.drawRect(crossColor, topLeft = Offset(cx - armWidth / 2f, cy - armLen / 2f), size = Size(armWidth, armLen))
        // Horizontal arm
        drawScope.drawRect(crossColor, topLeft = Offset(cx - armLen / 2f, cy - armWidth / 2f), size = Size(armLen, armWidth))
      }

      FlagStyle.CENTRAL_DISK -> {
        val bg = colors.getOrElse(0) { Color.White }
        val disk = colors.getOrElse(1) { Color.Red }
        drawScope.drawRect(bg, topLeft = Offset(left, top), size = Size(diameter, diameter))
        drawScope.drawCircle(disk, radius = r * 0.52f, center = Offset(cx, cy))
      }

      FlagStyle.STAR_AND_CRESCENT -> {
        val bg = colors.getOrElse(0) { Color.Red }
        val symColor = colors.getOrElse(1) { Color.White }
        drawScope.drawRect(bg, topLeft = Offset(left, top), size = Size(diameter, diameter))

        // Draw crescent moon
        val moonRadius = r * 0.44f
        drawScope.drawCircle(symColor, radius = moonRadius, center = Offset(cx - r * 0.08f, cy))
        drawScope.drawCircle(bg, radius = moonRadius * 0.82f, center = Offset(cx + r * 0.02f, cy))

        // Draw 5-pointed star
        drawStar(drawScope, cx + r * 0.28f, cy, r * 0.18f, symColor)
      }

      FlagStyle.CANTON_STRIPES -> {
        val c1 = colors.getOrElse(0) { Color.Red }
        val c2 = colors.getOrElse(1) { Color.White }
        val canton = colors.getOrElse(2) { Color.Blue }

        val stripeH = diameter / 9f
        for (i in 0 until 9) {
          val stripeColor = if (i % 2 == 0) c1 else c2
          drawScope.drawRect(stripeColor, topLeft = Offset(left, top + i * stripeH), size = Size(diameter, stripeH))
        }

        // Canton square in upper left
        val cantonW = diameter * 0.48f
        val cantonH = stripeH * 5f
        drawScope.drawRect(canton, topLeft = Offset(left, top), size = Size(cantonW, cantonH))

        // Stars inside canton
        drawStar(drawScope, left + cantonW * 0.5f, top + cantonH * 0.5f, r * 0.16f, Color.White)
      }

      FlagStyle.DIAGONAL_SALTIRE -> {
        // e.g. Jamaica, UK
        val bg1 = colors.getOrElse(0) { Color.Black }
        val bg2 = colors.getOrElse(1) { Color.Green }
        val cross = flag.accentColor

        drawScope.drawRect(bg1, topLeft = Offset(left, top), size = Size(diameter, diameter))

        // Upper and lower triangles
        val path1 = Path().apply {
          moveTo(left, top)
          lineTo(cx, cy)
          lineTo(left + diameter, top)
          close()
        }
        val path2 = Path().apply {
          moveTo(left, top + diameter)
          lineTo(cx, cy)
          lineTo(left + diameter, top + diameter)
          close()
        }
        drawScope.drawPath(path1, bg2)
        drawScope.drawPath(path2, bg2)

        // Diagonal saltire lines
        val strokeW = r * 0.28f
        drawScope.drawLine(cross, Offset(left, top), Offset(left + diameter, top + diameter), strokeWidth = strokeW)
        drawScope.drawLine(cross, Offset(left, top + diameter), Offset(left + diameter, top), strokeWidth = strokeW)
      }

      FlagStyle.TRIANGLE_CHEVRON -> {
        // e.g. Czechia, Cuba, South Africa
        val c1 = colors.getOrElse(0) { Color.White }
        val c2 = colors.getOrElse(1) { Color.Red }
        val chevron = colors.getOrElse(2) { Color.Blue }

        val halfH = r
        drawScope.drawRect(c1, topLeft = Offset(left, top), size = Size(diameter, halfH))
        drawScope.drawRect(c2, topLeft = Offset(left, cy), size = Size(diameter, halfH))

        val chevronPath = Path().apply {
          moveTo(left, top)
          lineTo(cx + r * 0.15f, cy)
          lineTo(left, top + diameter)
          close()
        }
        drawScope.drawPath(chevronPath, chevron)
        drawOptionalSymbol(drawScope, cx - r * 0.35f, cy, r * 0.6f, flag)
      }

      FlagStyle.SOLID_STAR -> {
        val bg = colors.getOrElse(0) { Color.Red }
        val starColor = colors.getOrElse(1) { Color.Yellow }
        drawScope.drawRect(bg, topLeft = Offset(left, top), size = Size(diameter, diameter))
        drawStar(drawScope, cx, cy, r * 0.5f, starColor)
      }

      FlagStyle.CENTRAL_EMBLEM -> {
        // e.g. Canada, Brazil, South Korea, Argentina
        when (flag.code) {
          "CA" -> {
            // Canada vertical bars (red, white, red) + maple leaf
            val w1 = diameter * 0.25f
            val w2 = diameter * 0.5f
            drawScope.drawRect(Color(0xFFFF0000), topLeft = Offset(left, top), size = Size(w1, diameter))
            drawScope.drawRect(Color(0xFFFFFFFF), topLeft = Offset(left + w1, top), size = Size(w2, diameter))
            drawScope.drawRect(Color(0xFFFF0000), topLeft = Offset(left + w1 + w2, top), size = Size(w1, diameter))
            drawStar(drawScope, cx, cy, r * 0.38f, Color(0xFFFF0000))
          }
          "BR" -> {
            // Brazil green field + yellow rhombus + blue circle
            drawScope.drawRect(Color(0xFF009C3B), topLeft = Offset(left, top), size = Size(diameter, diameter))
            val lozenge = Path().apply {
              moveTo(cx, top + r * 0.3f)
              lineTo(left + diameter * 0.9f, cy)
              lineTo(cx, top + diameter - r * 0.3f)
              lineTo(left + diameter * 0.1f, cy)
              close()
            }
            drawScope.drawPath(lozenge, Color(0xFFFFDF00))
            drawScope.drawCircle(Color(0xFF002776), radius = r * 0.38f, center = Offset(cx, cy))
          }
          "KR" -> {
            // South Korea white field + taegeuk circle
            drawScope.drawRect(Color.White, topLeft = Offset(left, top), size = Size(diameter, diameter))
            // Red top semicircle, blue bottom semicircle
            val taeRadius = r * 0.45f
            drawScope.drawCircle(Color(0xFFCD2E3A), radius = taeRadius, center = Offset(cx, cy))
            val blueHalf = Path().apply {
              moveTo(cx - taeRadius, cy)
              lineTo(cx + taeRadius, cy)
              arcTo(Rect(cx - taeRadius, cy - taeRadius, cx + taeRadius, cy + taeRadius), 0f, 180f, false)
              close()
            }
            drawScope.drawPath(blueHalf, Color(0xFF0047A0))
          }
          else -> {
            val bg = colors.getOrElse(0) { Color.White }
            drawScope.drawRect(bg, topLeft = Offset(left, top), size = Size(diameter, diameter))
            drawStar(drawScope, cx, cy, r * 0.4f, flag.accentColor)
          }
        }
      }
    }
  }

  private fun drawOptionalSymbol(
    drawScope: DrawScope,
    cx: Float,
    cy: Float,
    r: Float,
    flag: CountryFlag
  ) {
    if (flag.symbolType.isNotEmpty()) {
      when (flag.symbolType) {
        "chakra" -> {
          // India Ashoka Chakra (navy blue wheel with spokes)
          drawScope.drawCircle(
            color = flag.accentColor,
            radius = r * 0.22f,
            center = Offset(cx, cy),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.0f)
          )
          for (i in 0 until 12) {
            val a = (i * PI / 6.0).toFloat()
            drawScope.drawLine(
              color = flag.accentColor,
              start = Offset(cx, cy),
              end = Offset(cx + cos(a) * r * 0.21f, cy + sin(a) * r * 0.21f),
              strokeWidth = 1.2f
            )
          }
        }
        "sun" -> {
          // Yellow sun disk
          drawScope.drawCircle(color = flag.accentColor, radius = r * 0.22f, center = Offset(cx, cy))
        }
        "star" -> {
          drawStar(drawScope, cx, cy, r * 0.25f, flag.accentColor)
        }
        "stars" -> {
          drawStar(drawScope, cx - r * 0.15f, cy, r * 0.12f, flag.accentColor)
          drawStar(drawScope, cx + r * 0.15f, cy, r * 0.12f, flag.accentColor)
        }
        "cross" -> {
          val arm = r * 0.35f
          val w = r * 0.12f
          drawScope.drawRect(flag.accentColor, Offset(cx - w / 2f, cy - arm / 2f), Size(w, arm))
          drawScope.drawRect(flag.accentColor, Offset(cx - arm / 2f, cy - w / 2f), Size(arm, w))
        }
        else -> {
          // Default small emblem/star
          drawStar(drawScope, cx, cy, r * 0.2f, flag.accentColor)
        }
      }
    }
  }

  private fun drawStar(
    drawScope: DrawScope,
    cx: Float,
    cy: Float,
    radius: Float,
    color: Color
  ) {
    val path = Path()
    val innerRadius = radius * 0.42f
    val points = 5
    var angle = -PI / 2.0

    for (i in 0 until points * 2) {
      val r = if (i % 2 == 0) radius else innerRadius
      val x = (cx + cos(angle) * r).toFloat()
      val y = (cy + sin(angle) * r).toFloat()
      if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
      angle += PI / points
    }
    path.close()
    drawScope.drawPath(path, color)
  }
}
