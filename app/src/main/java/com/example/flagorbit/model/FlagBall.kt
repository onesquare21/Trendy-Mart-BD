package com.example.flagorbit.model

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import kotlin.math.sqrt

class FlagBall(
  val id: Int,
  val country: CountryFlag,
  var x: Float,
  var y: Float,
  var vx: Float,
  var vy: Float,
  var radius: Float,
  var mass: Float = 1.0f
) {
  var angle: Float = (Math.random() * Math.PI * 2).toFloat()
  var angularVelocity: Float = ((Math.random() - 0.5) * 1.5).toFloat()

  var isEligibleForElimination: Boolean = false
  var isEliminating: Boolean = false
  var eliminationTimer: Float = 0f
  var isEliminated: Boolean = false

  var flashIntensity: Float = 0f
  var pulsePhase: Float = (Math.random() * Math.PI * 2).toFloat()
  var isVictor: Boolean = false
  var victoryTimer: Float = 0f

  var spawnScale: Float = 0f // Animates from 0 to 1 at start of round

  // Physically believable subtle impact compression and elastic rebound
  var squashNormalX: Float = 1f
  var squashNormalY: Float = 0f
  var squashAmount: Float = 0f // 0f = normal sphere, up to ~1f (subtle deformation)
  var squashVelocity: Float = 0f // Elastic spring velocity

  // Real-time glass contact highlight glint at the point of impact
  var contactGlintIntensity: Float = 0f
  var contactGlintX: Float = 0f
  var contactGlintY: Float = 0f

  val speed: Float
    get() = sqrt(vx * vx + vy * vy)

  fun getCenter(): Offset = Offset(x, y)

  fun applyImpactCompression(nx: Float, ny: Float, intensity: Float, contactX: Float, contactY: Float) {
    squashNormalX = nx
    squashNormalY = ny

    // Subtle, realistic deformation proportional to impact strength (capped at 1.0f)
    val targetSquash = (intensity * 0.90f).coerceIn(0.12f, 1.0f)
    squashAmount = targetSquash.coerceAtLeast(squashAmount)
    squashVelocity = -targetSquash * 16f

    // Real-time glass specular contact glint
    contactGlintIntensity = (intensity * 1.15f).coerceIn(0.25f, 1.0f)
    contactGlintX = contactX
    contactGlintY = contactY
  }

  fun updateCompression(dt: Float) {
    if (squashAmount > 0.001f || abs(squashVelocity) > 0.01f) {
      // Damped harmonic spring: F = -k * x - c * v
      val k = 460f // High stiffness for glass marble (rapid rebound)
      val c = 32f  // Critical damping for clean recovery
      val acc = -k * squashAmount - c * squashVelocity
      squashVelocity += acc * dt
      squashAmount += squashVelocity * dt
      if (squashAmount < 0f && squashVelocity < 0f) {
        squashAmount = 0f
        squashVelocity = 0f
      }
    } else {
      squashAmount = 0f
      squashVelocity = 0f
    }

    if (contactGlintIntensity > 0f) {
      contactGlintIntensity = (contactGlintIntensity - dt * 12f).coerceAtLeast(0f)
    }
  }
}
