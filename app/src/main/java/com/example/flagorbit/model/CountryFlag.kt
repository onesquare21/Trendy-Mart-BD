package com.example.flagorbit.model

import androidx.compose.ui.graphics.Color

/**
 * Geometric styles for procedural national flag rendering.
 */
enum class FlagStyle {
  HORIZONTAL_2,    // e.g. Poland, Indonesia, Ukraine
  HORIZONTAL_3,    // e.g. Germany, Netherlands, Russia, Austria, Hungary, Armenia
  HORIZONTAL_3_DIFF, // e.g. Colombia (2:1:1)
  VERTICAL_2,      // e.g. Portugal (partial), Vatican
  VERTICAL_3,      // e.g. France, Italy, Belgium, Ireland, Romania, Nigeria, Chad, Peru
  NORDIC_CROSS,    // e.g. Sweden, Norway, Denmark, Finland, Iceland
  GREEK_CROSS,     // e.g. Switzerland, Greece
  CENTRAL_DISK,    // e.g. Japan, Bangladesh, Palau
  STAR_AND_CRESCENT, // e.g. Turkey, Pakistan, Tunisia, Algeria
  CENTRAL_EMBLEM,  // e.g. Argentina, Brazil, India, Canada, South Korea, Mexico
  CANTON_STRIPES,  // e.g. USA, Greece, Malaysia, Liberia, Uruguay
  DIAGONAL_SALTIRE,// e.g. Jamaica, Scotland
  TRIANGLE_CHEVRON,// e.g. Czechia, Philippines, Cuba, Jordan, Sudan
  SOLID_STAR       // e.g. Vietnam, China, Somalia, Morocco
}

/**
 * National flag definition with official ISO-2 code, nation name, primary colors, and style.
 */
data class CountryFlag(
  val code: String,
  val name: String,
  val style: FlagStyle,
  val colors: List<Color>,
  val accentColor: Color = Color.White,
  val symbolType: String = ""
) {
  val flagEmoji: String
    get() = countryCodeToEmoji(code)

  companion object {
    /**
     * Converts a 2-letter ISO country code (e.g. "BD", "US", "FR") into its Unicode flag emoji.
     */
    fun countryCodeToEmoji(countryCode: String): String {
      if (countryCode.length != 2) return ""
      val c1 = countryCode[0].uppercaseChar()
      val c2 = countryCode[1].uppercaseChar()
      if (c1 !in 'A'..'Z' || c2 !in 'A'..'Z') return ""
      val firstCodePoint = 0x1F1E6 + (c1 - 'A')
      val secondCodePoint = 0x1F1E6 + (c2 - 'A')
      return String(Character.toChars(firstCodePoint)) + String(Character.toChars(secondCodePoint))
    }
  }
}
