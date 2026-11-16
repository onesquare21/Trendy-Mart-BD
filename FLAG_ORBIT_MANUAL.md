# FLAG ORBIT — Mobile Physics Simulation Game

**FLAG ORBIT** is a minimalist, endless 2D physics simulation game featuring 25 vibrant national flag balls inside a circular arena. Built natively for Android with **Kotlin and Jetpack Compose**, it delivers 60–120 FPS hardware-accelerated physics, realistic collision dynamics, 3D spherical shading, procedural ASMR sound effects, and an endless elimination cycle.

---

## 🎮 Core Game Cycle

- **Endless Gameplay**: 25 Balls → 24 → 23 → ... → 2 → 1 Survivor → Golden Crown & Champion Celebration → Immediate 25 New Balls → Repeat Forever.
- **Round Duration**: Exactly 60 seconds per complete 25-to-1 elimination cycle.
- **High-Energy Velocity**: Faster, energetic ball physics (160–480 px/s) delivering intense collision frequency and dynamic movement.
- **Autonomous Ambient Simulation**: Runs automatically upon launch with zero manual menus, pause screens, or waiting screens.
- **Interactive Cosmic Impulse**: Tap anywhere on the screen to emit a glowing cosmic ripple that gently nudges nearby flag balls.

---

## 🌐 Flag Database & Previous Round Exclusion Rule

- **105+ Authentic National Flags**: Spanning Americas, Europe, Asia, Africa, Oceania, and the Middle East.
- **Strict Exclusion**: Flags used in the immediately preceding round are strictly excluded from the next round ($N=25$).
- **Fair Survivor Rotation**: Prevents repetitive outcomes by tracking recent survivors.

---

## ⚙️ Engineering & Physics Engine

1. **Sub-stepped Continuous Collision Physics & Realistic Impact Compression**:
   - 4 sub-steps per frame to prevent tunneling.
   - Elastic momentum exchange with restitution ($e = 0.985$) and tangential friction for realistic angular spin.
   - Physically believable impact compression: subtle 2–6% volume-conserving squash along the collision normal with instantaneous damped spring rebound (~80ms).
   - Real-time glass contact glint: brilliant diamond specular highlight flaring precisely at the collision interface.
   - Elegant micro-spark particles: tiny crystal dust sparks triggered strictly on stronger impacts ($> 0.52f$), with zero shockwaves or neon glare.
   - Circular arena clamping preventing balls from ever escaping.
   - Dynamic velocity conservation preventing frozen or stalled balls over the 60-second round.

2. **Last 5 Survival Drama**:
   - When down to 5 survivors, arena lighting intensity dynamically increases.
   - Subtle cinematic drama spotlight and heightened metallic bevel reflections focus attention on the final contenders.
   - Deeper, more impactful ASMR collision acoustic profile with lower fundamental resonance and maximum stereo punch.

3. **Realistic 3D Glass Marbles & Clean Aesthetic**:
   - Clean, glossy 3D glass marble appearance with pure black background.
   - Realistic spherical shading, vitreous depth, dual specular highlights, direct light glint, and Fresnel perimeter rims.
   - Authentic national flag vector graphics clearly recognizable within each ball.
   - Elimination & Victory Highlights: Subtle target aura when a ball is eligible for elimination, and brilliant golden radiance for the sole round victor.

4. **Collision-Triggered Elimination System**:
   - 25-to-1 elimination sequence scheduled across exactly 60 seconds (~2.4s average per elimination).
   - Only eligible balls break on impact, shattering into glowing sampled color fragments with motion trails.
   - When the final ball is crowned victor, the next round starts immediately with 25 new non-overlapping flags with zero delay.
   - Non-elimination bounces produce soft glass taps, ceramic clicks, rubber bounces, and subtle impact sparks.

5. **High-Fidelity Mixed ASMR Sound Engine & Spatial Audio**:
   - 8 distinct, masterfully synthesized acoustic profiles: Soft Glass Ring, Crystal Clink, Ceramic Tap, Rubber Bounce, Deep Marble Punch (140Hz–320Hz sub-core), Dense Ceramic Thock, Heavy Glass Sphere, and Delicate Glancing Contact.
   - 50–70% overall collision volume increase via elevated baseline playback gain (0.88f–1.0f) and high-drive soft-knee hyperbolic tangent (`tanh`) saturation, maximizing perceptual loudness on phone speakers while strictly preventing clipping or distortion.
   - Distinct 3-tier collision dispatch:
     - Light collisions (< 0.30 force): crisp, delicate crystal clinks and glancing contacts in higher registers (0.96f–1.12f).
     - Medium collisions (0.30–0.65 force): resonant glass rings, ceramic taps, and cushioned rubber bounces (0.90f–1.06f).
     - Heavy collisions (>= 0.65 force): deep solid marble punch and heavy glass sphere impact with powerful sub-bass depth (140Hz–340Hz) and lower pitches (0.80f–0.96f).
   - Equal-power spatial stereo panning: balances sound across both phone speakers (attenuation floor clamped to 0.72f) for clean localization without losing off-center collision loudness.
   - Dynamic anti-clutter polyphony management: sliding 28ms window rate-limits overlapping collisions, giving priority to high-energy hits and crystal shatters.
   - 3 upgraded crystal-glass shatter variations with tactile initial fracture crack (multi-stage transient burst) and 7 sparkling crystalline chime harmonics (740Hz–7800Hz).
   - Exactly one short, resonant ethereal victory chord per completed round.

6. **Keep Screen Awake (Continuous Foreground Display)**:
   - Utilizes Android `WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON` on the Activity Window.
   - Synchronized with app lifecycle: activated in `onResume()` and cleared in `onPause()` to prevent screen timeouts during gameplay while respecting device battery when backgrounded.
   - Dual-guaranteed in Jetpack Compose via `LocalView.current.keepScreenOn = true`.

7. **Golden Champion Crown & Winner Announcement**:
   - Dynamic winner detection: Identifies the surviving country ball automatically when down to 1 survivor.
   - Displays a majestic golden champion crown adorned with pearls and ruby jewels above the winning marble.
   - Prominently displays the nation's emoji and uppercase country name (e.g., `🇧🇩 BANGLADESH WINS!`).
   - Celebratory starburst particles and golden ripples accompany a harmonic victory chime.
   - Displayed for ~2 seconds with smooth entrance/exit animations, followed by an immediate autonomous transition to the next 25-ball round.
   - Strictly the only text exception; all normal gameplay remains 100% clean and text-free.

---

## 📱 Android Studio Integration & APK Build Instructions

### Method 1: Export directly from AI Studio
1. In the AI Studio interface, open the settings menu.
2. Select **Export as ZIP** or **Generate APK / AAB**.

### Method 2: Open in Android Studio
1. Clone or extract the project to your local computer.
2. Open **Android Studio** (Hedgehog, Iguana, Jellyfish, or newer).
3. Select **Open an Existing Project** and choose the project root folder.
4. Allow Gradle to sync dependencies automatically.
5. Connect your Android device via USB (or start an Android Virtual Device).
6. Click **Run** (`Shift + F10`) to build, install, and run on your smartphone.

### Method 3: Command-Line APK Build
Run the following command from the root directory:
```bash
# Build Debug APK
./gradlew assembleDebug

# Build Release APK
./gradlew assembleRelease
```
The generated APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk` or `app/build/outputs/apk/release/app-release.apk`.

Install directly to an attached phone via ADB:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 🚀 Automated CI/CD Workflow

An automated GitHub Actions workflow is provided in `.github/workflows/android-release.yml`. When pushed to GitHub, it automatically sets up JDK 17, compiles the release APK, and uploads it to GitHub Action Artifacts for 1-click download.
