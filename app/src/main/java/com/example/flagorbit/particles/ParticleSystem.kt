package com.example.flagorbit.particles

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

class Particle(
  var x: Float = 0f,
  var y: Float = 0f,
  var vx: Float = 0f,
  var vy: Float = 0f,
  var prevX: Float = 0f,
  var prevY: Float = 0f,
  var size: Float = 4f,
  var color: Color = Color.White,
  var alpha: Float = 1f,
  var life: Float = 1f,
  var maxLife: Float = 1f,
  var isAlive: Boolean = false,
  var isSpark: Boolean = false
) {
  fun update(dt: Float) {
    if (!isAlive) return
    prevX = x
    prevY = y
    x += vx * dt
    y += vy * dt

    // Subtle drag
    vx *= (1f - 1.2f * dt)
    vy *= (1f - 1.2f * dt)

    life -= dt
    if (life <= 0f) {
      isAlive = false
    } else {
      alpha = (life / maxLife).coerceIn(0f, 1f)
    }
  }
}

class ArenaRingRipple(
  var x: Float = 0f,
  var y: Float = 0f,
  var radius: Float = 10f,
  var maxRadius: Float = 50f,
  var color: Color = Color.White,
  var alpha: Float = 0.8f,
  var life: Float = 0.4f,
  var maxLife: Float = 0.4f,
  var isAlive: Boolean = false
) {
  fun update(dt: Float) {
    if (!isAlive) return
    life -= dt
    if (life <= 0f) {
      isAlive = false
    } else {
      val progress = 1f - (life / maxLife)
      radius += (maxRadius - radius) * 8f * dt
      alpha = (1f - progress).coerceIn(0f, 1f) * 0.7f
    }
  }
}

class ParticleSystem(private val maxParticles: Int = 300) {
  private val pool: Array<Particle> = Array(maxParticles) { Particle() }
  private val ripples: Array<ArenaRingRipple> = Array(20) { ArenaRingRipple() }

  val activeParticles: List<Particle>
    get() = pool.filter { it.isAlive }

  val activeRipples: List<ArenaRingRipple>
    get() = ripples.filter { it.isAlive }

  fun spawnShatter(
    centerX: Float,
    centerY: Float,
    baseColors: List<Color>,
    speedMultiplier: Float = 1.0f
  ) {
    val count = 20
    var spawned = 0
    val colorCount = baseColors.size

    for (p in pool) {
      if (!p.isAlive) {
        val angle = (Math.random() * Math.PI * 2).toFloat()
        val speed = ((120f + Math.random() * 240f) * speedMultiplier).toFloat()
        val chosenColor = baseColors[spawned % colorCount]
        val life = (0.7f + Math.random() * 0.5f).toFloat()

        p.x = centerX + cos(angle) * 8f
        p.y = centerY + sin(angle) * 8f
        p.prevX = centerX
        p.prevY = centerY
        p.vx = cos(angle) * speed
        p.vy = sin(angle) * speed
        p.size = (3.5f + Math.random() * 4.5f).toFloat()
        p.color = chosenColor
        p.alpha = 1f
        p.life = life
        p.maxLife = life
        p.isAlive = true
        p.isSpark = false

        spawned++
        if (spawned >= count) break
      }
    }

    // Also spawn a subtle shockwave ripple
    spawnRipple(centerX, centerY, baseColors.firstOrNull() ?: Color.White, 70f)
  }

  fun spawnSparks(x: Float, y: Float, normalX: Float, normalY: Float, color: Color) {
    val count = 4
    var spawned = 0
    val baseAngle = kotlin.math.atan2(normalY, normalX)

    for (p in pool) {
      if (!p.isAlive) {
        val spread = ((Math.random() - 0.5) * 1.4).toFloat()
        val angle = baseAngle + spread
        val speed = (80f + Math.random() * 140f).toFloat()
        val life = (0.25f + Math.random() * 0.2f).toFloat()

        p.x = x
        p.y = y
        p.prevX = x
        p.prevY = y
        p.vx = cos(angle) * speed
        p.vy = sin(angle) * speed
        p.size = (2f + Math.random() * 2.5f).toFloat()
        p.color = color
        p.alpha = 0.9f
        p.life = life
        p.maxLife = life
        p.isAlive = true
        p.isSpark = true

        spawned++
        if (spawned >= count) break
      }
    }
  }

  /**
   * Spawns tiny, elegant micro-spark particles exclusively for stronger impacts.
   * Subtle, clean, short-lived (100-160ms) crystal dust.
   */
  fun spawnMicroSparks(x: Float, y: Float, normalX: Float, normalY: Float, intensity: Float) {
    if (intensity < 0.52f) return

    val count = if (intensity > 0.82f) 4 else 2
    var spawned = 0
    val baseAngle = kotlin.math.atan2(normalY, normalX)

    for (p in pool) {
      if (!p.isAlive) {
        val spread = ((Math.random() - 0.5) * 1.1).toFloat()
        val angle = baseAngle + spread
        val speed = (70f + Math.random() * 95f).toFloat() * intensity
        val life = (0.10f + Math.random() * 0.06f).toFloat() // 100-160ms micro lifespan

        p.x = x
        p.y = y
        p.prevX = x
        p.prevY = y
        p.vx = cos(angle) * speed
        p.vy = sin(angle) * speed
        p.size = (1.1f + Math.random() * 1.1f).toFloat() // Tiny micro spark (1.1 - 2.2 px)
        p.color = if (Math.random() < 0.65) Color.White else Color(0xFFE2F1FF)
        p.alpha = 0.90f
        p.life = life
        p.maxLife = life
        p.isAlive = true
        p.isSpark = true

        spawned++
        if (spawned >= count) break
      }
    }
  }

  fun spawnRipple(x: Float, y: Float, color: Color, maxRadius: Float = 60f) {
    for (r in ripples) {
      if (!r.isAlive) {
        r.x = x
        r.y = y
        r.radius = 8f
        r.maxRadius = maxRadius
        r.color = color
        r.alpha = 0.75f
        r.life = 0.45f
        r.maxLife = 0.45f
        r.isAlive = true
        break
      }
    }
  }

  fun spawnCelebration(centerX: Float, centerY: Float, colors: List<Color>) {
    val count = 36
    var spawned = 0
    val colorList = if (colors.isNotEmpty()) colors + listOf(Color(0xFFFFD700), Color.White) else listOf(Color(0xFFFFD700), Color.White, Color.Cyan)
    val colorCount = colorList.size

    for (p in pool) {
      if (!p.isAlive) {
        val angle = (Math.random() * Math.PI * 2).toFloat()
        val speed = (130f + Math.random() * 250f).toFloat()
        val chosenColor = colorList[spawned % colorCount]
        val life = (1.2f + Math.random() * 0.8f).toFloat()

        p.x = centerX
        p.y = centerY
        p.prevX = centerX
        p.prevY = centerY
        p.vx = cos(angle) * speed
        p.vy = sin(angle) * speed
        p.size = (3.5f + Math.random() * 4.5f).toFloat()
        p.color = chosenColor
        p.alpha = 1f
        p.life = life
        p.maxLife = life
        p.isAlive = true
        p.isSpark = true

        spawned++
        if (spawned >= count) break
      }
    }

    // Spawn dual celebratory golden & radiant rings
    spawnRipple(centerX, centerY, Color(0xFFFFD700), 130f)
    spawnRipple(centerX, centerY, Color.White, 90f)
  }

  fun update(dt: Float) {
    for (p in pool) {
      if (p.isAlive) p.update(dt)
    }
    for (r in ripples) {
      if (r.isAlive) r.update(dt)
    }
  }

  fun clear() {
    for (p in pool) p.isAlive = false
    for (r in ripples) r.isAlive = false
  }
}
