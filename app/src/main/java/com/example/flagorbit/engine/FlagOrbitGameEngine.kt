package com.example.flagorbit.engine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.flagorbit.audio.GameAudioManager
import com.example.flagorbit.model.CountryFlag
import com.example.flagorbit.model.FlagBall
import com.example.flagorbit.model.FlagDatabase
import com.example.flagorbit.model.WinnerInfo
import com.example.flagorbit.particles.ParticleSystem
import com.example.flagorbit.physics.PhysicsEngine
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class FlagOrbitGameEngine(
  private val audioManager: GameAudioManager
) {

  val physicsEngine = PhysicsEngine()
  companion object {
    const val BALLS_PER_ROUND = 25
    const val TARGET_ROUND_DURATION = 60.0f // Exactly 60s total elimination cycle
  }

  val particleSystem = ParticleSystem(maxParticles = 350)

  val balls = mutableListOf<FlagBall>()
  val previousRoundCodes = mutableSetOf<String>()
  val recentSurvivors = mutableListOf<String>()

  var roundNumber: Int = 1
    private set
  var roundElapsedTime: Float = 0f
    private set
  var currentSurvivorCount: Int = BALLS_PER_ROUND
    private set
  var lastSurvivor: CountryFlag? = null
    private set

  var winnerAnnouncement by mutableStateOf<WinnerInfo?>(null)
  var transitionTimer by mutableFloatStateOf(0f)
    private set

  private var timeUntilNextElimination: Float = 1.8f
  private var scheduledEliminationCount: Int = 0
  private var isTransitioningRounds: Boolean = false

  fun setupArena(centerX: Float, centerY: Float, radius: Float) {
    val changed = (physicsEngine.arenaCenterX != centerX ||
                   physicsEngine.arenaCenterY != centerY ||
                   physicsEngine.arenaRadius != radius)

    physicsEngine.arenaCenterX = centerX
    physicsEngine.arenaCenterY = centerY
    physicsEngine.arenaRadius = radius

    if (balls.isEmpty() && radius > 20f) {
      startNewRound()
    } else if (changed && balls.isNotEmpty()) {
      // Adjust existing balls to fit resized arena proportionally
      for (ball in balls) {
        val dx = ball.x - centerX
        val dy = ball.y - centerY
        val dist = sqrt(dx * dx + dy * dy)
        val maxDist = radius - ball.radius
        if (dist > maxDist && dist > 0.001f) {
          ball.x = centerX + (dx / dist) * maxDist
          ball.y = centerY + (dy / dist) * maxDist
        }
      }
    }
  }

  fun startNewRound() {
    isTransitioningRounds = false
    transitionTimer = 0f
    winnerAnnouncement = null
    roundElapsedTime = 0f
    scheduledEliminationCount = 0

    // PREVIOUS ROUND EXCLUSION RULE:
    // Strictly pick 25 flags that were NOT in the previous round
    val selectedFlags = FlagDatabase.pickNextRoundFlags(previousRoundCodes, recentSurvivors)

    // Update previous round memory for the next round
    previousRoundCodes.clear()
    previousRoundCodes.addAll(selectedFlags.map { it.code })

    balls.clear()
    val arenaR = physicsEngine.arenaRadius
    val cx = physicsEngine.arenaCenterX
    val cy = physicsEngine.arenaCenterY

    // Optimal ball size: scalable based on arena radius for 25 balls
    val ballRadius = (arenaR * 0.080f).coerceIn(18f, 38f)

    // Position each of the 25 balls avoiding overlap
    val placedPositions = mutableListOf<Offset>()
    val maxSpawnRadius = arenaR - ballRadius - 12f

    for (i in 0 until BALLS_PER_ROUND) {
      val flag = selectedFlags[i]
      var spawnPos = Offset.Zero
      var attempts = 0
      var validPos = false

      while (attempts < 200 && !validPos) {
        // Random radial distribution
        val angle = (Math.random() * Math.PI * 2).toFloat()
        val r = (Math.random() * maxSpawnRadius).toFloat()
        val candidate = Offset(cx + cos(angle) * r, cy + sin(angle) * r)

        validPos = true
        for (other in placedPositions) {
          val dist = (candidate - other).getDistance()
          if (dist < ballRadius * 2.15f) {
            validPos = false
            break
          }
        }
        if (validPos) {
          spawnPos = candidate
        }
        attempts++
      }

      if (!validPos) {
        // Concentric ring backup placement
        val ringAngle = (i * (Math.PI * 2.0 / BALLS_PER_ROUND.toDouble())).toFloat()
        val ringRadius = maxSpawnRadius * 0.65f
        spawnPos = Offset(cx + cos(ringAngle) * ringRadius, cy + sin(ringAngle) * ringRadius)
      }

      placedPositions.add(spawnPos)

      val moveAngle = (Math.random() * Math.PI * 2).toFloat()
      val initialSpeed = (180f + Math.random() * 120f).toFloat()
      val ball = FlagBall(
        id = i,
        country = flag,
        x = spawnPos.x,
        y = spawnPos.y,
        vx = cos(moveAngle) * initialSpeed,
        vy = sin(moveAngle) * initialSpeed,
        radius = ballRadius
      )
      ball.spawnScale = 0.05f
      balls.add(ball)
    }

    currentSurvivorCount = BALLS_PER_ROUND
    timeUntilNextElimination = 1.8f + (Math.random().toFloat() * 0.4f) // Initial fast battle start
  }

  fun update(dt: Float) {
    if (balls.isEmpty() || physicsEngine.arenaRadius <= 0f) return

    roundElapsedTime += dt

    // 1. Update ball spawn animations, pulses, and elastic compression rebound
    for (ball in balls) {
      if (ball.spawnScale < 1.0f) {
        ball.spawnScale = (ball.spawnScale + dt * 4.5f).coerceAtMost(1.0f)
      }
      ball.pulsePhase += dt * 4.5f
      if (ball.flashIntensity > 0f) {
        ball.flashIntensity = (ball.flashIntensity - dt * 6f).coerceAtLeast(0f)
      }
      ball.updateCompression(dt)
    }

    // 2. Physics simulation
    physicsEngine.update(balls, dt)

    // 3. Handle collision events: sound, micro-sparks, and collision-triggered eliminations
    val sortedEvents = physicsEngine.collisionEvents.sortedByDescending { it.intensity }
    val arenaR = physicsEngine.arenaRadius
    val arenaCx = physicsEngine.arenaCenterX
    val isFinalFive = currentSurvivorCount in 2..5

    for (event in sortedEvents) {
      val pan = if (arenaR > 0f) {
        ((event.point.x - arenaCx) / arenaR).coerceIn(-1.0f, 1.0f)
      } else 0f
      // Spatial ASMR audio playback synchronized with collision strength
      audioManager.playCollisionSound(event.intensity, pan, isFinalFive = isFinalFive)

      // Tiny, elegant micro-spark particles ONLY for stronger impacts (no shockwaves, no excessive neon)
      if (event.intensity > 0.52f) {
        particleSystem.spawnMicroSparks(event.point.x, event.point.y, event.normal.x, event.normal.y, event.intensity)
      }

      // Check for collision-triggered elimination
      val candidateA = event.ballA
      val candidateB = event.ballB

      if (candidateA.isEligibleForElimination && !candidateA.isEliminated && currentSurvivorCount > 1) {
        eliminateBall(candidateA, event.point)
      } else if (candidateB != null && candidateB.isEligibleForElimination && !candidateB.isEliminated && currentSurvivorCount > 1) {
        eliminateBall(candidateB, event.point)
      }
    }

    // 4. Particle system update
    particleSystem.update(dt)

    // 5. Elimination Scheduler (25 balls eliminated over exactly 60 seconds)
    if (!isTransitioningRounds && currentSurvivorCount > 1) {
      timeUntilNextElimination -= dt

      if (timeUntilNextElimination <= 0f) {
        // Find an active ball to make eligible for elimination
        val activeBalls = balls.filter { !it.isEliminated && !it.isEligibleForElimination }
        if (activeBalls.isNotEmpty() && currentSurvivorCount > 1) {
          val chosen = activeBalls.random()
          chosen.isEligibleForElimination = true
          chosen.pulsePhase = 0f
          scheduledEliminationCount++
        }

        // Dynamically pace remaining eliminations across the 60-second duration (~2.4s average)
        val remainingEliminations = (currentSurvivorCount - 1).coerceAtLeast(1)
        val remainingTargetTime = (TARGET_ROUND_DURATION - roundElapsedTime).coerceAtLeast(0.5f)
        val targetInterval = (remainingTargetTime / remainingEliminations).coerceIn(1.75f, 2.75f)
        timeUntilNextElimination = targetInterval + ((Math.random().toFloat() - 0.5f) * 0.25f)
      }

      // Fallback safeguard: If an eligible ball hasn't collided within 1.35s, trigger it softly to maintain exact 60s round pace
      val lingeringEligible = balls.find { it.isEligibleForElimination && !it.isEliminated }
      if (lingeringEligible != null && timeUntilNextElimination < -1.35f && currentSurvivorCount > 1) {
        eliminateBall(lingeringEligible, Offset(lingeringEligible.x, lingeringEligible.y))
      }
    }

    // 6. Last Survivor & Dynamic Winner Announcement Celebration
    if (currentSurvivorCount == 1 && !isTransitioningRounds) {
      val soleSurvivor = balls.firstOrNull { !it.isEliminated }
      if (soleSurvivor != null) {
        soleSurvivor.isVictor = true
        lastSurvivor = soleSurvivor.country
        recentSurvivors.add(soleSurvivor.country.code)
        if (recentSurvivors.size > 25) recentSurvivors.removeAt(0)

        // Dynamic Winner Detection: ISO emoji + dynamic uppercase country name
        val emoji = soleSurvivor.country.flagEmoji
        val announcement = "$emoji ${soleSurvivor.country.name.uppercase()} WINS!"
        winnerAnnouncement = WinnerInfo(
          country = soleSurvivor.country,
          flagEmoji = emoji,
          announcementText = announcement,
          roundNumber = roundNumber
        )

        // Celebratory particle fireworks around victor
        particleSystem.spawnCelebration(
          centerX = soleSurvivor.x,
          centerY = soleSurvivor.y,
          colors = soleSurvivor.country.colors
        )

        // Exactly one short, satisfying victory sound per completed round
        audioManager.playVictorySound()
        isTransitioningRounds = true
        transitionTimer = 0f
      }
    }

    if (isTransitioningRounds) {
      transitionTimer += dt

      // Spawn subtle celebratory sparkles around victor during announcement
      if (transitionTimer < 1.9f && Math.random() < 0.40) {
        val soleSurvivor = balls.firstOrNull { !it.isEliminated }
        if (soleSurvivor != null) {
          particleSystem.spawnSparks(
            soleSurvivor.x,
            soleSurvivor.y,
            ((Math.random() - 0.5) * 2).toFloat(),
            ((Math.random() - 0.5) * 2).toFloat(),
            Color(0xFFFFD700)
          )
        }
      }

      // Display the winner announcement for approximately 2 seconds,
      // then automatically start the next 25-ball round with no manual interaction.
      if (transitionTimer >= 2.0f) {
        winnerAnnouncement = null
        roundNumber++
        startNewRound()
      }
    }
  }

  private fun eliminateBall(ball: FlagBall, impactPoint: Offset, isFinalSurvivor: Boolean = false) {
    if (ball.isEliminated || (!isFinalSurvivor && currentSurvivorCount <= 1)) return

    ball.isEligibleForElimination = false
    ball.isEliminated = true
    if (currentSurvivorCount > 0) {
      currentSurvivorCount--
    }

    // Shatter sound with spatial panning
    val pan = if (physicsEngine.arenaRadius > 0f) {
      ((ball.x - physicsEngine.arenaCenterX) / physicsEngine.arenaRadius).coerceIn(-1.0f, 1.0f)
    } else 0f
    audioManager.playShatterSound(pan)

    // Neon shatter particles
    val colors = ball.country.colors
    particleSystem.spawnShatter(
      centerX = ball.x,
      centerY = ball.y,
      baseColors = if (colors.isNotEmpty()) colors else listOf(Color.White, Color.Cyan)
    )
  }

  fun onUserTouch(x: Float, y: Float) {
    // Gentle cosmetic cosmic ripple that pushes nearby balls
    physicsEngine.applyRadialImpulse(balls, x, y, 140f)
    particleSystem.spawnRipple(x, y, Color(0xFF4488FF), 75f)
  }

  fun release() {
    audioManager.release()
    balls.clear()
    particleSystem.clear()
  }
}
