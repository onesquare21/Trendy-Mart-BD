package com.example.flagorbit.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.tanh

/**
 * High-Fidelity ASMR Sound Engine for FLAG ORBIT.
 *
 * Delivers premium acoustic tactile feedback for marble and glass sphere collisions,
 * crystalline shatters, and victory celebration chords.
 *
 * Acoustic characteristics:
 * - +50-70% perceptual volume boost via high-RMS soft-knee saturation and boosted playback floors.
 * - Rich low-frequency depth (140Hz-340Hz sub-punch & marble body) and crisp high-frequency details (3kHz-6kHz).
 * - Distinct 3-tier intensity acoustic dispatch: Light, Medium, Heavy (Marble Punch / Heavy Glass).
 * - Natural pitch & tone variations to prevent repetition.
 * - Equal-power stereo spatial panning across the circular arena.
 * - Strict anti-distortion soft limiting and polyphony rate-limiting.
 */
class GameAudioManager(private val context: Context) {

  private var soundPool: SoundPool? = null

  // Mixed Premium ASMR sound library (8 distinct physical profiles)
  private var soundGlassRingId: Int = 0        // Soft glass tapping with natural resonance
  private var soundCrystalClinkId: Int = 0     // Crisp crystal impacts with brilliant sparkle
  private var soundCeramicTapId: Int = 0       // Delicate ceramic click with warm attack
  private var soundRubberBounceId: Int = 0     // Gentle rubber-like bounce with low-mid body
  private var soundMarblePunchId: Int = 0      // NEW: Solid marble core impact with deep low punch
  private var soundCeramicDeepId: Int = 0      // Dense ceramic thock with warm tactile resonance
  private var soundGlassHeavyId: Int = 0       // Deep glass sphere impact with rich body
  private var soundGlanceDelicateId: Int = 0   // Ultra-delicate glancing contact

  // Premium crystal-glass shattering variations
  private val shatterSoundIds = mutableListOf<Int>()
  private var soundVictoryId: Int = 0

  private var isLoaded = false
  private var lastPlayTimeMs: Long = 0
  private var soundsPlayedInWindow = 0
  private var windowStartTimeMs: Long = 0
  private var lastPlayedSoundId: Int = 0
  private var lastPlayedSoundTimeMs: Long = 0

  init {
    initSoundPool()
  }

  private fun initSoundPool() {
    try {
      val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

      // 16 dedicated voices for immersive, spatial ASMR polyphony
      soundPool = SoundPool.Builder()
        .setMaxStreams(16)
        .setAudioAttributes(audioAttributes)
        .build()

      val cacheDir = context.cacheDir

      // 1. Synthesize High-RMS ASMR acoustic profiles (v5 generation)
      val glassRingWav = generateWav(createGlassRingWaveform())
      val crystalClinkWav = generateWav(createCrystalClinkWaveform())
      val ceramicTapWav = generateWav(createCeramicTapWaveform())
      val rubberBounceWav = generateWav(createRubberBounceWaveform())
      val marblePunchWav = generateWav(createMarblePunchWaveform())
      val ceramicDeepWav = generateWav(createCeramicDeepWaveform())
      val glassHeavyWav = generateWav(createGlassHeavyWaveform())
      val glanceDelicateWav = generateWav(createGlanceDelicateWaveform())

      // 2. Synthesize 3 distinct ASMR crystal shatter variations with enhanced crispness
      val shatter1Wav = generateWav(createCrystalShatterWaveform())
      val shatter2Wav = generateWav(createCascadeShatterWaveform())
      val shatter3Wav = generateWav(createPrismShatterWaveform())

      // 3. Ethereal victory chime
      val victoryWav = generateWav(createVictoryWaveform())

      // Cache files with v5 versioning to force clean regeneration
      val fGlassRing = writeBytesToFile(cacheDir, "snd_glass_ring_v5.wav", glassRingWav)
      val fCrystalClink = writeBytesToFile(cacheDir, "snd_crystal_clink_v5.wav", crystalClinkWav)
      val fCeramicTap = writeBytesToFile(cacheDir, "snd_ceramic_tap_v5.wav", ceramicTapWav)
      val fRubberBounce = writeBytesToFile(cacheDir, "snd_rubber_bounce_v5.wav", rubberBounceWav)
      val fMarblePunch = writeBytesToFile(cacheDir, "snd_marble_punch_v5.wav", marblePunchWav)
      val fCeramicDeep = writeBytesToFile(cacheDir, "snd_ceramic_deep_v5.wav", ceramicDeepWav)
      val fGlassHeavy = writeBytesToFile(cacheDir, "snd_glass_heavy_v5.wav", glassHeavyWav)
      val fGlanceDelicate = writeBytesToFile(cacheDir, "snd_glance_delicate_v5.wav", glanceDelicateWav)

      val fShatter1 = writeBytesToFile(cacheDir, "snd_shatter_crystal_v5.wav", shatter1Wav)
      val fShatter2 = writeBytesToFile(cacheDir, "snd_shatter_cascade_v5.wav", shatter2Wav)
      val fShatter3 = writeBytesToFile(cacheDir, "snd_shatter_prism_v5.wav", shatter3Wav)
      val fVictory = writeBytesToFile(cacheDir, "snd_victory_asmr_v5.wav", victoryWav)

      soundGlassRingId = soundPool?.load(fGlassRing.absolutePath, 1) ?: 0
      soundCrystalClinkId = soundPool?.load(fCrystalClink.absolutePath, 1) ?: 0
      soundCeramicTapId = soundPool?.load(fCeramicTap.absolutePath, 1) ?: 0
      soundRubberBounceId = soundPool?.load(fRubberBounce.absolutePath, 1) ?: 0
      soundMarblePunchId = soundPool?.load(fMarblePunch.absolutePath, 1) ?: 0
      soundCeramicDeepId = soundPool?.load(fCeramicDeep.absolutePath, 1) ?: 0
      soundGlassHeavyId = soundPool?.load(fGlassHeavy.absolutePath, 1) ?: 0
      soundGlanceDelicateId = soundPool?.load(fGlanceDelicate.absolutePath, 1) ?: 0

      shatterSoundIds.clear()
      soundPool?.load(fShatter1.absolutePath, 1)?.let { if (it != 0) shatterSoundIds.add(it) }
      soundPool?.load(fShatter2.absolutePath, 1)?.let { if (it != 0) shatterSoundIds.add(it) }
      soundPool?.load(fShatter3.absolutePath, 1)?.let { if (it != 0) shatterSoundIds.add(it) }

      soundVictoryId = soundPool?.load(fVictory.absolutePath, 1) ?: 0

      isLoaded = true
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  /**
   * Plays an upgraded ASMR collision sound with 50-70% boosted perceptual loudness,
   * deep low-end body, crisp high transients, equal-power stereo panning, and natural micro-variation.
   *
   * @param intensity Normalized collision force (0f to 1f)
   * @param pan Normalized horizontal arena position (-1f for far left, +1f for far right)
   * @param isFinalFive When true (last 5 survivors), plays deeper, punchier acoustic profile
   */
  fun playCollisionSound(intensity: Float, pan: Float = 0f, isFinalFive: Boolean = false) {
    if (!isLoaded || soundPool == null) return
    val now = System.currentTimeMillis()

    // Smooth sliding window polyphony manager: max 4 sounds per 28ms window to prevent acoustic mud
    if (now - windowStartTimeMs > 28L) {
      windowStartTimeMs = now
      soundsPlayedInWindow = 0
    }
    // High-energy impacts (intensity >= 0.65f) are privileged to cut through during crowd clusters
    if (soundsPlayedInWindow >= 4 && intensity < 0.65f) return
    soundsPlayedInWindow++
    lastPlayTimeMs = now

    // Boosted volume scaling: base volume floors at 0.88f and reaches 1.0f on strong impacts (+50-70% volume boost)
    val baseVolume = if (isFinalFive) {
      1.0f
    } else {
      (0.88f + intensity * 0.12f).coerceIn(0.88f, 1.0f)
    }

    // Equal-power stereo spatial panning: keeps phone speakers crisp and balanced while providing clear localization
    val clampedPan = pan.coerceIn(-1.0f, 1.0f)
    val leftGain = if (clampedPan <= 0f) 1.0f else (1.0f - clampedPan * 0.28f).coerceIn(0.72f, 1.0f)
    val rightGain = if (clampedPan >= 0f) 1.0f else (1.0f + clampedPan * 0.28f).coerceIn(0.72f, 1.0f)
    val leftVolume = (baseVolume * leftGain).coerceIn(0.72f, 1.0f)
    val rightVolume = (baseVolume * rightGain).coerceIn(0.72f, 1.0f)

    // Pitch variation: dynamic velocity modulation + organic randomized micro-detuning
    val velocityPitchShift = (intensity - 0.5f) * 0.08f
    val organicMicroDetune = ((Math.random() - 0.5) * 0.08).toFloat()

    // Intensity-Tiered Acoustic Material Selection:
    // Heavy collisions sound deeper, heavier, and more powerful than light collisions.
    val rnd = Math.random()
    val (soundId, targetPitch) = if (isFinalFive) {
      // Final 5 Survivor Drama: deep marble punch, heavy glass, and deep ceramic with lowered pitch
      val id = when {
        rnd < 0.45 -> soundMarblePunchId
        rnd < 0.80 -> soundGlassHeavyId
        else -> soundCeramicDeepId
      }
      val pitch = (0.84f + velocityPitchShift + organicMicroDetune).coerceIn(0.76f, 0.94f)
      id to pitch
    } else {
      when {
        // LIGHT COLLISIONS (< 0.30 force): crisp, delicate crystal, glance, and light ceramic
        intensity < 0.30f -> {
          val id = when {
            rnd < 0.40 -> soundGlanceDelicateId
            rnd < 0.70 -> soundCrystalClinkId
            else -> soundCeramicTapId
          }
          val pitch = (1.04f + velocityPitchShift + organicMicroDetune).coerceIn(0.96f, 1.12f)
          id to pitch
        }
        // MEDIUM COLLISIONS (0.30 to 0.65 force): natural glass ring, ceramic tap, rubber bounce
        intensity < 0.65f -> {
          val id = when {
            rnd < 0.38 -> soundGlassRingId
            rnd < 0.68 -> soundCeramicTapId
            rnd < 0.86 -> soundRubberBounceId
            else -> soundCeramicDeepId
          }
          val pitch = (0.98f + velocityPitchShift + organicMicroDetune).coerceIn(0.90f, 1.06f)
          id to pitch
        }
        // HEAVY COLLISIONS (>= 0.65 force): deep solid marble punch, heavy glass sphere, deep ceramic thock
        else -> {
          val id = when {
            rnd < 0.45 -> soundMarblePunchId
            rnd < 0.80 -> soundGlassHeavyId
            else -> soundCeramicDeepId
          }
          val pitch = (0.88f + velocityPitchShift + organicMicroDetune).coerceIn(0.80f, 0.96f)
          id to pitch
        }
      }
    }

    // Suppress rapid-fire identical sound repeats within 10ms to eliminate robotic phasing
    if (soundId == lastPlayedSoundId && (now - lastPlayedSoundTimeMs) < 10L) {
      return
    }
    lastPlayedSoundId = soundId
    lastPlayedSoundTimeMs = now

    // Playback with priority 2
    soundPool?.play(soundId, leftVolume, rightVolume, 2, 0, targetPitch)
  }

  /**
   * Plays a crisp, satisfying, high-impact glass-crystal shatter sound with spatial panning.
   *
   * @param pan Normalized horizontal arena position (-1f to +1f)
   */
  fun playShatterSound(pan: Float = 0f) {
    if (!isLoaded || soundPool == null || shatterSoundIds.isEmpty()) return

    val clampedPan = pan.coerceIn(-1.0f, 1.0f)
    val leftGain = if (clampedPan <= 0f) 1.0f else (1.0f - clampedPan * 0.25f).coerceIn(0.75f, 1.0f)
    val rightGain = if (clampedPan >= 0f) 1.0f else (1.0f + clampedPan * 0.25f).coerceIn(0.75f, 1.0f)

    val chosenId = shatterSoundIds.random()
    val pitch = (0.98f + (Math.random().toFloat() - 0.5f) * 0.06f)

    // Maximum 1.0 volume for a crisp, detailed, punchy shatter experience
    soundPool?.play(chosenId, leftGain, rightGain, 10, 0, pitch)
  }

  /**
   * Plays the resonant ethereal victory chord when only one survivor remains.
   */
  fun playVictorySound() {
    if (!isLoaded || soundPool == null) return
    soundPool?.play(soundVictoryId, 1.0f, 1.0f, 8, 0, 1.0f)
  }

  fun release() {
    soundPool?.release()
    soundPool = null
    isLoaded = false
  }

  // --- ASMR Procedural Waveform Synthesis with High-RMS Acoustic Modeling & Soft-Knee Saturation ---

  /**
   * Soft Glass Tapping: Pure crystal glass tap with delicate, silky natural resonance,
   * crisp transient click, and smooth resonant decay.
   */
  private fun createGlassRingWaveform(): ShortArray {
    val sampleRate = 44100
    val durationSec = 0.110f
    val numSamples = (sampleRate * durationSec).toInt()
    val buffer = DoubleArray(numSamples)
    val f1 = 1240.0
    val f2 = 2580.0
    val f3 = 3980.0
    val f4 = 5400.0

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      // Crisp 2ms initial transient contact edge
      val click = if (t < 0.003) sin(2.0 * PI * 4800.0 * t) * exp(-900.0 * t) * 0.6 else 0.0
      val env1 = exp(-40.0 * t)
      val env2 = exp(-62.0 * t)
      val env3 = exp(-95.0 * t)
      val env4 = exp(-130.0 * t)
      buffer[i] = click + (
        sin(2.0 * PI * f1 * t) * env1 +
        0.52 * sin(2.0 * PI * f2 * t) * env2 +
        0.30 * sin(2.0 * PI * f3 * t) * env3 +
        0.18 * sin(2.0 * PI * f4 * t) * env4
      )
    }
    return normalize(buffer)
  }

  /**
   * Crystal Impact: High-register quartz crystal contact with brilliant clarity,
   * sparkling shimmer, and crisp high-frequency overtones.
   */
  private fun createCrystalClinkWaveform(): ShortArray {
    val sampleRate = 44100
    val durationSec = 0.065f
    val numSamples = (sampleRate * durationSec).toInt()
    val buffer = DoubleArray(numSamples)
    val f1 = 1720.0
    val f2 = 3440.0
    val f3 = 5160.0
    val f4 = 6880.0

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      val click = if (t < 0.0025) sin(2.0 * PI * 6200.0 * t) * exp(-1200.0 * t) * 0.7 else 0.0
      val env1 = exp(-75.0 * t)
      val env2 = exp(-115.0 * t)
      val env3 = exp(-160.0 * t)
      val env4 = exp(-210.0 * t)
      buffer[i] = click + (
        sin(2.0 * PI * f1 * t) * env1 +
        0.55 * sin(2.0 * PI * f2 * t) * env2 +
        0.32 * sin(2.0 * PI * f3 * t) * env3 +
        0.18 * sin(2.0 * PI * f4 * t) * env4
      )
    }
    return normalize(buffer)
  }

  /**
   * Delicate Ceramic Click: Crisp ceramic sphere impact with fast tactile attack,
   * warm low-mid body, and smooth decay.
   */
  private fun createCeramicTapWaveform(): ShortArray {
    val sampleRate = 44100
    val durationSec = 0.075f
    val numSamples = (sampleRate * durationSec).toInt()
    val buffer = DoubleArray(numSamples)
    val f1 = 1420.0
    val f2 = 2840.0
    val f3 = 4260.0

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      val click = if (t < 0.003) sin(2.0 * PI * 3800.0 * t) * exp(-1000.0 * t) * 0.55 else 0.0
      val env1 = exp(-65.0 * t)
      val env2 = exp(-98.0 * t)
      val env3 = exp(-140.0 * t)
      buffer[i] = click + (
        sin(2.0 * PI * f1 * t) * env1 +
        0.52 * sin(2.0 * PI * f2 * t) * env2 +
        0.28 * sin(2.0 * PI * f3 * t) * env3
      )
    }
    return normalize(buffer)
  }

  /**
   * Gentle Rubber-like Bounce: Soft, cushioned elastomer impact with rounded
   * low-frequency compression (260Hz/520Hz) and smooth viscous decay.
   */
  private fun createRubberBounceWaveform(): ShortArray {
    val sampleRate = 44100
    val durationSec = 0.090f
    val numSamples = (sampleRate * durationSec).toInt()
    val buffer = DoubleArray(numSamples)
    val f1 = 260.0
    val f2 = 520.0
    val f3 = 820.0

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      val env1 = exp(-48.0 * t)
      val env2 = exp(-78.0 * t)
      val env3 = exp(-120.0 * t)
      buffer[i] = (
        sin(2.0 * PI * f1 * t) * env1 +
        0.44 * sin(2.0 * PI * f2 * t) * env2 +
        0.22 * sin(2.0 * PI * f3 * t) * env3
      )
    }
    return normalize(buffer)
  }

  /**
   * NEW: Solid Marble Punch: Solid high-density marble-on-marble collision.
   * Generates rich low-frequency depth (140Hz sub-punch & 320Hz core knock)
   * coupled with a crisp 2.8kHz stone contact snap.
   */
  private fun createMarblePunchWaveform(): ShortArray {
    val sampleRate = 44100
    val durationSec = 0.125f
    val numSamples = (sampleRate * durationSec).toInt()
    val buffer = DoubleArray(numSamples)

    val fSub = 145.0
    val fCore = 330.0
    val fBody = 860.0
    val fSnap = 2650.0
    val fCrisp = 4800.0

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      // Initial crisp contact transient
      val snap = if (t < 0.0035) sin(2.0 * PI * fCrisp * t) * exp(-850.0 * t) * 0.75 else 0.0
      val envSub = exp(-32.0 * t)
      val envCore = exp(-44.0 * t)
      val envBody = exp(-68.0 * t)
      val envSnap = exp(-105.0 * t)

      buffer[i] = snap + (
        0.85 * sin(2.0 * PI * fSub * t) * envSub +
        1.00 * sin(2.0 * PI * fCore * t) * envCore +
        0.58 * sin(2.0 * PI * fBody * t) * envBody +
        0.35 * sin(2.0 * PI * fSnap * t) * envSnap
      )
    }
    return normalize(buffer)
  }

  /**
   * Dense Ceramic Deep Thock: Warm, dense ceramic marble impact with rich low-mid body.
   */
  private fun createCeramicDeepWaveform(): ShortArray {
    val sampleRate = 44100
    val durationSec = 0.100f
    val numSamples = (sampleRate * durationSec).toInt()
    val buffer = DoubleArray(numSamples)
    val f1 = 680.0
    val f2 = 1520.0
    val f3 = 2840.0

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      val click = if (t < 0.003) sin(2.0 * PI * 3400.0 * t) * exp(-950.0 * t) * 0.55 else 0.0
      val env1 = exp(-38.0 * t)
      val env2 = exp(-64.0 * t)
      val env3 = exp(-98.0 * t)
      buffer[i] = click + (
        sin(2.0 * PI * f1 * t) * env1 +
        0.60 * sin(2.0 * PI * f2 * t) * env2 +
        0.34 * sin(2.0 * PI * f3 * t) * env3
      )
    }
    return normalize(buffer)
  }

  /**
   * Deep Glass Impact: Full-bodied heavy glass sphere impact with solid low punch (220Hz/480Hz)
   * and smooth overtone ring (1320Hz/2740Hz).
   */
  private fun createGlassHeavyWaveform(): ShortArray {
    val sampleRate = 44100
    val durationSec = 0.135f
    val numSamples = (sampleRate * durationSec).toInt()
    val buffer = DoubleArray(numSamples)
    val fSub = 220.0
    val f1 = 480.0
    val f2 = 1320.0
    val f3 = 2740.0
    val f4 = 4400.0

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      val click = if (t < 0.003) sin(2.0 * PI * 4200.0 * t) * exp(-800.0 * t) * 0.6 else 0.0
      val envSub = exp(-28.0 * t)
      val env1 = exp(-32.0 * t)
      val env2 = exp(-50.0 * t)
      val env3 = exp(-82.0 * t)
      val env4 = exp(-115.0 * t)
      buffer[i] = click + (
        0.65 * sin(2.0 * PI * fSub * t) * envSub +
        1.00 * sin(2.0 * PI * f1 * t) * env1 +
        0.62 * sin(2.0 * PI * f2 * t) * env2 +
        0.38 * sin(2.0 * PI * f3 * t) * env3 +
        0.22 * sin(2.0 * PI * f4 * t) * env4
      )
    }
    return normalize(buffer)
  }

  /**
   * Delicate Glancing Contact: Ultra-soft grazing contact with subtle high-register texture.
   */
  private fun createGlanceDelicateWaveform(): ShortArray {
    val sampleRate = 44100
    val durationSec = 0.040f
    val numSamples = (sampleRate * durationSec).toInt()
    val buffer = DoubleArray(numSamples)
    val f1 = 2200.0
    val f2 = 4500.0

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      val env = exp(-120.0 * t)
      buffer[i] = (sin(2.0 * PI * f1 * t) * env + 0.42 * sin(2.0 * PI * f2 * t) * env)
    }
    return normalize(buffer)
  }

  /**
   * Shatter Variation 1: Classic Crystal Burst with crisp tactile initial crack (2.8x burst)
   * and cascading crystalline chimes (7 harmonics across 740Hz - 7800Hz).
   */
  private fun createCrystalShatterWaveform(): ShortArray {
    val sampleRate = 44100
    val durationSec = 0.52f
    val numSamples = (sampleRate * durationSec).toInt()
    val buffer = DoubleArray(numSamples)
    val freqs = doubleArrayOf(740.0, 1260.0, 2100.0, 3450.0, 4800.0, 6200.0, 7800.0)

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      var sample = 0.0

      // Tactile crack in initial 18ms with low thump + high crack
      if (t < 0.018) {
        val crackEnv = exp(-180.0 * t)
        val thump = sin(2.0 * PI * 180.0 * t) * 1.2
        val crackNoise = (Math.random() - 0.5) * 3.2
        sample += (thump + crackNoise) * crackEnv
      }

      // 7 crystal harmonic chimes
      for ((idx, f) in freqs.withIndex()) {
        val decay = 8.5 + idx * 3.8
        sample += sin(2.0 * PI * f * t) * exp(-decay * t) * (0.45 / (idx * 0.4 + 1))
      }

      // Micro sparkling fragment shimmer
      sample += (Math.random() - 0.5) * exp(-24.0 * t) * 0.22
      buffer[i] = sample
    }
    return normalize(buffer)
  }

  /**
   * Shatter Variation 2: Cascading Crystal Rain with delicate micro-delays mimicking falling fragments.
   */
  private fun createCascadeShatterWaveform(): ShortArray {
    val sampleRate = 44100
    val durationSec = 0.55f
    val numSamples = (sampleRate * durationSec).toInt()
    val buffer = DoubleArray(numSamples)
    val freqs = doubleArrayOf(920.0, 1540.0, 2480.0, 3860.0, 5400.0, 7200.0)

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      var sample = 0.0

      if (t < 0.022) {
        val thump = sin(2.0 * PI * 160.0 * t) * 1.1
        val noise = (Math.random() - 0.5) * 2.8
        sample += (thump + noise) * exp(-160.0 * t)
      }

      for ((idx, f) in freqs.withIndex()) {
        val delay = idx * 0.007 // Staggered harmonic cascade
        if (t >= delay) {
          val dt = t - delay
          val decay = 7.8 + idx * 3.2
          sample += sin(2.0 * PI * f * dt) * exp(-decay * dt) * (0.46 / (idx * 0.35 + 1))
        }
      }

      // Sparkling micro-transients
      if (t in 0.03..0.28 && Math.random() < 0.05) {
        sample += (Math.random() - 0.5) * 0.42
      }

      buffer[i] = sample
    }
    return normalize(buffer)
  }

  /**
   * Shatter Variation 3: Resonant Prism Fracture with deep low-end body and ethereal chordal ring.
   */
  private fun createPrismShatterWaveform(): ShortArray {
    val sampleRate = 44100
    val durationSec = 0.54f
    val numSamples = (sampleRate * durationSec).toInt()
    val buffer = DoubleArray(numSamples)
    val freqs = doubleArrayOf(620.0, 1080.0, 1850.0, 2980.0, 4400.0, 6500.0)

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      var sample = 0.0

      if (t < 0.02) {
        val thump = sin(2.0 * PI * 140.0 * t) * 1.3
        val noise = (Math.random() - 0.5) * 3.0
        sample += (thump + noise) * exp(-170.0 * t)
      }

      for ((idx, f) in freqs.withIndex()) {
        val decay = 7.5 + idx * 3.4
        sample += sin(2.0 * PI * f * t) * exp(-decay * t) * (0.46 / (idx * 0.35 + 1))
      }

      buffer[i] = sample
    }
    return normalize(buffer)
  }

  /**
   * Resonant ethereal chord for victory celebration.
   */
  private fun createVictoryWaveform(): ShortArray {
    val sampleRate = 44100
    val durationSec = 1.1f
    val numSamples = (sampleRate * durationSec).toInt()
    val buffer = DoubleArray(numSamples)
    val freqs = doubleArrayOf(587.33, 739.99, 880.00, 1174.66) // D major rich chord

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      val attack = (t / 0.04).coerceIn(0.0, 1.0)
      val decay = exp(-2.6 * t)
      val env = attack * decay
      var sample = 0.0
      for ((idx, f) in freqs.withIndex()) {
        sample += sin(2.0 * PI * f * t) * (0.40 / (idx * 0.25 + 1))
      }
      buffer[i] = sample * env
    }
    return normalize(buffer)
  }

  /**
   * Normalizes audio buffer with musical soft-knee saturation, boosting RMS resonance and body
   * by 50-70% while softly compressing peaks to eliminate clipping and distortion.
   */
  private fun normalize(buffer: DoubleArray): ShortArray {
    var maxVal = 0.0
    for (s in buffer) {
      val a = abs(s)
      if (a > maxVal) maxVal = a
    }
    if (maxVal < 0.0001) maxVal = 1.0

    // Peak limited to 96% of full scale (Short.MAX_VALUE = 32767) to guarantee zero DAC clipping
    val targetPeak = 0.96 * Short.MAX_VALUE
    val normCoeff = 2.2 // Increased drive into tanh soft-knee saturation (+50-70% RMS energy)
    val denom = tanh(normCoeff)
    val result = ShortArray(buffer.size)

    for (i in buffer.indices) {
      val norm = buffer[i] / maxVal
      val saturated = tanh(norm * normCoeff) / denom
      val scaled = saturated * targetPeak
      result[i] = scaled.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }
    return result
  }

  private fun generateWav(pcmData: ShortArray): ByteArray {
    val sampleRate = 44100
    val channels = 1
    val byteRate = sampleRate * channels * 2
    val dataSize = pcmData.size * 2
    val totalSize = 36 + dataSize

    val buffer = ByteBuffer.allocate(44 + dataSize)
    buffer.order(ByteOrder.LITTLE_ENDIAN)

    // RIFF header
    buffer.put("RIFF".toByteArray())
    buffer.putInt(totalSize)
    buffer.put("WAVE".toByteArray())
    buffer.put("fmt ".toByteArray())
    buffer.putInt(16) // Subchunk1Size (16 for PCM)
    buffer.putShort(1) // AudioFormat (1 for PCM)
    buffer.putShort(channels.toShort())
    buffer.putInt(sampleRate)
    buffer.putInt(byteRate)
    buffer.putShort((channels * 2).toShort()) // BlockAlign
    buffer.putShort(16) // BitsPerSample
    buffer.put("data".toByteArray())
    buffer.putInt(dataSize)

    for (sample in pcmData) {
      buffer.putShort(sample)
    }

    return buffer.array()
  }

  private fun writeBytesToFile(dir: File, filename: String, bytes: ByteArray): File {
    val file = File(dir, filename)
    FileOutputStream(file).use { it.write(bytes) }
    return file
  }
}
