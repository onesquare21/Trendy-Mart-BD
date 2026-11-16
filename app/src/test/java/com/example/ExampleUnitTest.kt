package com.example

import com.example.flagorbit.model.FlagBall
import com.example.flagorbit.model.FlagDatabase
import com.example.flagorbit.physics.PhysicsEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun flag_database_has_over_100_countries() {
    assertTrue(
      "Flag database must have at least 100 flags",
      FlagDatabase.ALL_FLAGS.size >= 100
    )
  }

  @Test
  fun previous_round_exclusion_rule_is_strictly_enforced() {
    val round1 = FlagDatabase.pickNextRoundFlags(emptySet(), emptyList())
    assertEquals(25, round1.size)
    assertEquals(25, round1.map { it.code }.toSet().size)

    val round1Codes = round1.map { it.code }.toSet()
    val round2 = FlagDatabase.pickNextRoundFlags(round1Codes, emptyList())
    assertEquals(25, round2.size)
    assertEquals(25, round2.map { it.code }.toSet().size)

    val round2Codes = round2.map { it.code }.toSet()
    val overlap = round1Codes.intersect(round2Codes)
    assertTrue("Round 2 must not contain any flags from Round 1 (strict 25-flag exclusion)", overlap.isEmpty())
  }

  @Test
  fun physics_engine_keeps_balls_inside_circular_arena() {
    val engine = PhysicsEngine()
    engine.arenaCenterX = 200f
    engine.arenaCenterY = 200f
    engine.arenaRadius = 150f

    val ball = FlagBall(
      id = 1,
      country = FlagDatabase.ALL_FLAGS.first(),
      x = 340f, // Outside arena
      y = 200f,
      vx = 200f,
      vy = 0f,
      radius = 15f
    )

    // Run physics update
    engine.update(listOf(ball), 0.016f)

    val distFromCenter = sqrt((ball.x - 200f) * (ball.x - 200f) + (ball.y - 200f) * (ball.y - 200f))
    assertTrue(
      "Ball must remain contained inside arena: dist ($distFromCenter) <= arenaRadius - radius (${150f - 15f})",
      distFromCenter <= 150f - 15f + 0.1f
    )
  }

  @Test
  fun country_code_to_emoji_and_announcement_text_is_accurate() {
    val bangladesh = FlagDatabase.ALL_FLAGS.first { it.code == "BD" }
    assertEquals("🇧🇩", bangladesh.flagEmoji)
    val announcement = "${bangladesh.flagEmoji} ${bangladesh.name.uppercase()} WINS!"
    assertEquals("🇧🇩 BANGLADESH WINS!", announcement)

    val usa = FlagDatabase.ALL_FLAGS.first { it.code == "US" }
    assertEquals("🇺🇸", usa.flagEmoji)
    assertEquals("🇺🇸 UNITED STATES WINS!", "${usa.flagEmoji} ${usa.name.uppercase()} WINS!")
  }

  @Test
  fun round_duration_and_ball_count_targets_match_spec() {
    assertEquals(25, com.example.flagorbit.engine.FlagOrbitGameEngine.BALLS_PER_ROUND)
    assertEquals(60.0f, com.example.flagorbit.engine.FlagOrbitGameEngine.TARGET_ROUND_DURATION, 0.001f)
  }

  @Test
  fun impact_compression_and_spring_rebound_recovers_smoothly() {
    val ball = FlagBall(
      id = 1,
      country = FlagDatabase.ALL_FLAGS.first(),
      x = 100f,
      y = 100f,
      vx = 0f,
      vy = 0f,
      radius = 20f
    )

    // Trigger impact compression
    ball.applyImpactCompression(1f, 0f, 0.8f, 120f, 100f)
    assertTrue("Squash amount should be positive on impact", ball.squashAmount > 0f)
    assertTrue("Contact glint should be positive on impact", ball.contactGlintIntensity > 0f)

    // Simulate 160ms of spring recovery
    for (step in 0 until 10) {
      ball.updateCompression(0.016f)
    }

    assertTrue("Squash amount should recover towards 0 after 160ms", ball.squashAmount < 0.15f)
  }
}
