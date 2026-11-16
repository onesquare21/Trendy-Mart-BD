package com.example.flagorbit.physics

import androidx.compose.ui.geometry.Offset
import com.example.flagorbit.model.FlagBall
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class CollisionEvent(
  val ballA: FlagBall,
  val ballB: FlagBall?, // null if boundary collision
  val point: Offset,
  val normal: Offset,
  val intensity: Float // 0f to 1f normalized collision force
)

class PhysicsEngine {

  var arenaCenterX: Float = 0f
  var arenaCenterY: Float = 0f
  var arenaRadius: Float = 300f

  private val restitution: Float = 0.985f
  private val minSpeed: Float = 160f
  private val maxSpeed: Float = 480f

  val collisionEvents = mutableListOf<CollisionEvent>()

  fun update(balls: List<FlagBall>, dt: Float) {
    collisionEvents.clear()
    if (balls.isEmpty() || arenaRadius <= 0f) return

    val subSteps = 4
    val subDt = (dt / subSteps).coerceIn(0.001f, 0.016f)

    for (step in 0 until subSteps) {
      // 1. Move balls & integrate rotation
      for (ball in balls) {
        if (ball.isEliminated) continue

        ball.x += ball.vx * subDt
        ball.y += ball.vy * subDt
        ball.angle += ball.angularVelocity * subDt
        ball.angularVelocity *= (1.0f - 0.02f * subDt)

        // Maintain organic speed floor so the simulation never dies or stalls
        val currentSpeed = ball.speed
        if (currentSpeed < minSpeed && currentSpeed > 0.001f) {
          val boost = (minSpeed / currentSpeed)
          ball.vx += ball.vx * (boost - 1f) * 0.1f
          ball.vy += ball.vy * (boost - 1f) * 0.1f
        } else if (currentSpeed > maxSpeed) {
          val damping = maxSpeed / currentSpeed
          ball.vx *= damping
          ball.vy *= damping
        }
      }

      // 2. Arena Boundary Collisions
      for (ball in balls) {
        if (ball.isEliminated) continue

        val dx = ball.x - arenaCenterX
        val dy = ball.y - arenaCenterY
        val dist = sqrt(dx * dx + dy * dy)
        val maxDist = arenaRadius - ball.radius

        if (dist > maxDist && dist > 0.001f) {
          // Ball is penetrating or touching boundary
          val nx = -dx / dist // Normal pointing inward toward center
          val ny = -dy / dist
          val penetration = dist - maxDist

          // Correct position
          ball.x += nx * penetration
          ball.y += ny * penetration

          // Velocity reflection
          val vDotN = ball.vx * nx + ball.vy * ny
          if (vDotN < 0) { // Moving outward towards the wall
            val tx = -ny
            val ty = nx
            val vDotT = ball.vx * tx + ball.vy * ty

            // Reflect along normal with restitution
            ball.vx = -restitution * vDotN * nx + 0.98f * vDotT * tx
            ball.vy = -restitution * vDotN * ny + 0.98f * vDotT * ty

            // Impart slight angular spin from wall friction
            ball.angularVelocity += vDotT * 0.003f

            val contactPoint = Offset(ball.x - nx * ball.radius, ball.y - ny * ball.radius)
            val intensity = (abs(vDotN) / maxSpeed).coerceIn(0.1f, 1.0f)
            ball.applyImpactCompression(-nx, -ny, intensity * 0.75f, contactPoint.x, contactPoint.y)

            collisionEvents.add(
              CollisionEvent(
                ballA = ball,
                ballB = null,
                point = contactPoint,
                normal = Offset(nx, ny),
                intensity = intensity
              )
            )
          }
        }
      }

      // 3. Ball-to-Ball Collisions
      val ballCount = balls.size
      for (i in 0 until ballCount) {
        val ball1 = balls[i]
        if (ball1.isEliminated) continue

        for (j in i + 1 until ballCount) {
          val ball2 = balls[j]
          if (ball2.isEliminated) continue

          val dx = ball2.x - ball1.x
          val dy = ball2.y - ball1.y
          val distSq = dx * dx + dy * dy
          val minDist = ball1.radius + ball2.radius

          if (distSq < minDist * minDist) {
            val dist = sqrt(distSq)
            if (dist < 0.001f) continue

            val nx = dx / dist
            val ny = dy / dist
            val penetration = minDist - dist

            // Separate overlapping balls evenly
            ball1.x -= nx * penetration * 0.5f
            ball1.y -= ny * penetration * 0.5f
            ball2.x += nx * penetration * 0.5f
            ball2.y += ny * penetration * 0.5f

            // Relative velocity
            val rvx = ball2.vx - ball1.vx
            val rvy = ball2.vy - ball1.vy
            val velAlongNormal = rvx * nx + rvy * ny

            // Only resolve if balls are moving towards each other
            if (velAlongNormal < 0) {
              val invMass1 = 1f / ball1.mass
              val invMass2 = 1f / ball2.mass
              val impulseMagnitude = -(1f + restitution) * velAlongNormal / (invMass1 + invMass2)

              val impulseX = impulseMagnitude * nx
              val impulseY = impulseMagnitude * ny

              ball1.vx -= impulseX * invMass1
              ball1.vy -= impulseY * invMass1
              ball2.vx += impulseX * invMass2
              ball2.vy += impulseY * invMass2

              // Tangential friction and rotational spin
              val tx = -ny
              val ty = nx
              val relVelTangential = rvx * tx + rvy * ty
              val frictionTorque = relVelTangential * 0.005f
              ball1.angularVelocity += frictionTorque
              ball2.angularVelocity -= frictionTorque

              val contactPoint = Offset(ball1.x + nx * ball1.radius, ball1.y + ny * ball1.radius)
              val intensity = (abs(velAlongNormal) / maxSpeed).coerceIn(0.12f, 1.0f)

              // Subtle impact compression & contact glint reflection
              ball1.applyImpactCompression(nx, ny, intensity, contactPoint.x, contactPoint.y)
              ball2.applyImpactCompression(-nx, -ny, intensity, contactPoint.x, contactPoint.y)

              collisionEvents.add(
                CollisionEvent(
                  ballA = ball1,
                  ballB = ball2,
                  point = contactPoint,
                  normal = Offset(nx, ny),
                  intensity = intensity
                )
              )
            }
          }
        }
      }
    }
  }

  /**
   * Applies a gentle interactive cosmic ripple force when the user touches the screen.
   */
  fun applyRadialImpulse(balls: List<FlagBall>, touchX: Float, touchY: Float, strength: Float = 120f) {
    for (ball in balls) {
      if (ball.isEliminated) continue
      val dx = ball.x - touchX
      val dy = ball.y - touchY
      val dist = sqrt(dx * dx + dy * dy)
      if (dist in 1f..180f) {
        val factor = (1f - dist / 180f)
        val nx = dx / dist
        val ny = dy / dist
        ball.vx += nx * strength * factor
        ball.vy += ny * strength * factor
        ball.angularVelocity += (Math.random().toFloat() - 0.5f) * 2f
      }
    }
  }
}
