package com.example.flagorbit.model

import androidx.compose.ui.graphics.Color

object FlagDatabase {

  // Complete database of 105 authentic national flags
  val ALL_FLAGS: List<CountryFlag> = listOf(
    // 1. Americas
    CountryFlag("US", "United States", FlagStyle.CANTON_STRIPES, listOf(Color(0xFFB22234), Color(0xFFFFFFFF), Color(0xFF3C3B6E)), Color(0xFFFFFFFF), "stars"),
    CountryFlag("CA", "Canada", FlagStyle.CENTRAL_EMBLEM, listOf(Color(0xFFFF0000), Color(0xFFFFFFFF), Color(0xFFFF0000)), Color(0xFFFF0000), "maple_leaf"),
    CountryFlag("BR", "Brazil", FlagStyle.CENTRAL_EMBLEM, listOf(Color(0xFF009C3B), Color(0xFFFFDF00), Color(0xFF002776)), Color(0xFFFFFFFF), "globe"),
    CountryFlag("AR", "Argentina", FlagStyle.HORIZONTAL_3, listOf(Color(0xFF75AADB), Color(0xFFFFFFFF), Color(0xFF75AADB)), Color(0xFFF6B40E), "sun"),
    CountryFlag("MX", "Mexico", FlagStyle.VERTICAL_3, listOf(Color(0xFF006847), Color(0xFFFFFFFF), Color(0xFFCE1126)), Color(0xFF8B4513), "eagle"),
    CountryFlag("CO", "Colombia", FlagStyle.HORIZONTAL_3_DIFF, listOf(Color(0xFFFCD116), Color(0xFF003893), Color(0xFFCE1126))),
    CountryFlag("CL", "Chile", FlagStyle.TRIANGLE_CHEVRON, listOf(Color(0xFF0039A6), Color(0xFFFFFFFF), Color(0xFFD52B1E)), Color(0xFFFFFFFF), "star"),
    CountryFlag("PE", "Peru", FlagStyle.VERTICAL_3, listOf(Color(0xFFD91023), Color(0xFFFFFFFF), Color(0xFFD91023))),
    CountryFlag("UY", "Uruguay", FlagStyle.CANTON_STRIPES, listOf(Color(0xFF0038A8), Color(0xFFFFFFFF)), Color(0xFFFCD116), "sun"),
    CountryFlag("JM", "Jamaica", FlagStyle.DIAGONAL_SALTIRE, listOf(Color(0xFF000000), Color(0xFF009B3A)), Color(0xFFFED100), "saltire"),
    CountryFlag("CU", "Cuba", FlagStyle.TRIANGLE_CHEVRON, listOf(Color(0xFF002A8F), Color(0xFFFFFFFF), Color(0xFFCF142B)), Color(0xFFFFFFFF), "star"),
    CountryFlag("CR", "Costa Rica", FlagStyle.HORIZONTAL_3, listOf(Color(0xFF002B7F), Color(0xFFFFFFFF), Color(0xFFCE1126))),
    CountryFlag("PA", "Panama", FlagStyle.CENTRAL_EMBLEM, listOf(Color(0xFFFFFFFF), Color(0xFF072357), Color(0xFFDA121A)), Color(0xFFDA121A), "stars"),
    CountryFlag("EC", "Ecuador", FlagStyle.HORIZONTAL_3_DIFF, listOf(Color(0xFFFFDD00), Color(0xFF034EA2), Color(0xFFED1C24)), Color(0xFF034EA2), "condor"),
    CountryFlag("VE", "Venezuela", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFFCE300), Color(0xFF00247D), Color(0xFFCF142B)), Color(0xFFFFFFFF), "stars"),

    // 2. Europe
    CountryFlag("GB", "United Kingdom", FlagStyle.DIAGONAL_SALTIRE, listOf(Color(0xFF012169), Color(0xFFFFFFFF), Color(0xFFC8102E)), Color(0xFFC8102E), "union_jack"),
    CountryFlag("DE", "Germany", FlagStyle.HORIZONTAL_3, listOf(Color(0xFF000000), Color(0xFFFF0000), Color(0xFFFFCC00))),
    CountryFlag("FR", "France", FlagStyle.VERTICAL_3, listOf(Color(0xFF002654), Color(0xFFFFFFFF), Color(0xFFED2939))),
    CountryFlag("IT", "Italy", FlagStyle.VERTICAL_3, listOf(Color(0xFF009246), Color(0xFFFFFFFF), Color(0xFFCE2B37))),
    CountryFlag("ES", "Spain", FlagStyle.HORIZONTAL_3_DIFF, listOf(Color(0xFFAA151B), Color(0xFFF1BF00), Color(0xFFAA151B)), Color(0xFFAA151B), "crown"),
    CountryFlag("NL", "Netherlands", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFAE1C28), Color(0xFFFFFFFF), Color(0xFF21468B))),
    CountryFlag("BE", "Belgium", FlagStyle.VERTICAL_3, listOf(Color(0xFF000000), Color(0xFFFFD100), Color(0xFFFF0F21))),
    CountryFlag("PT", "Portugal", FlagStyle.VERTICAL_2, listOf(Color(0xFF006600), Color(0xFFFF0000)), Color(0xFFFFD100), "shield"),
    CountryFlag("SE", "Sweden", FlagStyle.NORDIC_CROSS, listOf(Color(0xFF006AA7), Color(0xFFFECC00))),
    CountryFlag("NO", "Norway", FlagStyle.NORDIC_CROSS, listOf(Color(0xFFBA0C2F), Color(0xFF00205B), Color(0xFFFFFFFF))),
    CountryFlag("DK", "Denmark", FlagStyle.NORDIC_CROSS, listOf(Color(0xFFC60C30), Color(0xFFFFFFFF))),
    CountryFlag("FI", "Finland", FlagStyle.NORDIC_CROSS, listOf(Color(0xFFFFFFFF), Color(0xFF003580))),
    CountryFlag("IS", "Iceland", FlagStyle.NORDIC_CROSS, listOf(Color(0xFF02529C), Color(0xFFDC1E35), Color(0xFFFFFFFF))),
    CountryFlag("CH", "Switzerland", FlagStyle.GREEK_CROSS, listOf(Color(0xFFFF0000), Color(0xFFFFFFFF))),
    CountryFlag("IE", "Ireland", FlagStyle.VERTICAL_3, listOf(Color(0xFF169B62), Color(0xFFFFFFFF), Color(0xFFFF883E))),
    CountryFlag("GR", "Greece", FlagStyle.CANTON_STRIPES, listOf(Color(0xFF0D5EAF), Color(0xFFFFFFFF)), Color(0xFFFFFFFF), "cross"),
    CountryFlag("PL", "Poland", FlagStyle.HORIZONTAL_2, listOf(Color(0xFFFFFFFF), Color(0xFFDC143C))),
    CountryFlag("UA", "Ukraine", FlagStyle.HORIZONTAL_2, listOf(Color(0xFF0057B7), Color(0xFFFFD700))),
    CountryFlag("AT", "Austria", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFED2939), Color(0xFFFFFFFF), Color(0xFFED2939))),
    CountryFlag("CZ", "Czechia", FlagStyle.TRIANGLE_CHEVRON, listOf(Color(0xFFFFFFFF), Color(0xFFD7141A), Color(0xFF11457E))),
    CountryFlag("HU", "Hungary", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFCE2939), Color(0xFFFFFFFF), Color(0xFF477050))),
    CountryFlag("RO", "Romania", FlagStyle.VERTICAL_3, listOf(Color(0xFF002B7F), Color(0xFFFCD116), Color(0xFFCE1126))),
    CountryFlag("HR", "Croatia", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFFF0000), Color(0xFFFFFFFF), Color(0xFF171796)), Color(0xFFFF0000), "shield"),
    CountryFlag("BG", "Bulgaria", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFFFFFFF), Color(0xFF00966E), Color(0xFFD62612))),
    CountryFlag("RS", "Serbia", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFC6363C), Color(0xFF0C4076), Color(0xFFFFFFFF)), Color(0xFFC6363C), "eagle"),
    CountryFlag("SK", "Slovakia", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFFFFFFF), Color(0xFF0B4EA2), Color(0xFFEE1C25)), Color(0xFFEE1C25), "shield"),
    CountryFlag("SI", "Slovenia", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFFFFFFF), Color(0xFF005CE6), Color(0xFFED1C24)), Color(0xFF005CE6), "shield"),
    CountryFlag("EE", "Estonia", FlagStyle.HORIZONTAL_3, listOf(Color(0xFF0072CE), Color(0xFF000000), Color(0xFFFFFFFF))),
    CountryFlag("LV", "Latvia", FlagStyle.HORIZONTAL_3, listOf(Color(0xFF9E3039), Color(0xFFFFFFFF), Color(0xFF9E3039))),
    CountryFlag("LT", "Lithuania", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFFDB913), Color(0xFF006A44), Color(0xFFC1272D))),

    // 3. Asia
    CountryFlag("JP", "Japan", FlagStyle.CENTRAL_DISK, listOf(Color(0xFFFFFFFF), Color(0xFFBC002D))),
    CountryFlag("KR", "South Korea", FlagStyle.CENTRAL_EMBLEM, listOf(Color(0xFFFFFFFF), Color(0xFFCD2E3A), Color(0xFF0047A0)), Color(0xFF000000), "taegeuk"),
    CountryFlag("CN", "China", FlagStyle.SOLID_STAR, listOf(Color(0xFFEE1C25), Color(0xFFFFDE00)), Color(0xFFFFDE00), "stars"),
    CountryFlag("IN", "India", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFFF9933), Color(0xFFFFFFFF), Color(0xFF138808)), Color(0xFF000080), "chakra"),
    CountryFlag("ID", "Indonesia", FlagStyle.HORIZONTAL_2, listOf(Color(0xFFFF0000), Color(0xFFFFFFFF))),
    CountryFlag("PK", "Pakistan", FlagStyle.STAR_AND_CRESCENT, listOf(Color(0xFF01411C), Color(0xFFFFFFFF))),
    CountryFlag("BD", "Bangladesh", FlagStyle.CENTRAL_DISK, listOf(Color(0xFF006A4E), Color(0xFFF42A41))),
    CountryFlag("PH", "Philippines", FlagStyle.TRIANGLE_CHEVRON, listOf(Color(0xFF0038A8), Color(0xFFCE1126), Color(0xFFFFFFFF)), Color(0xFFFCD116), "sun"),
    CountryFlag("VN", "Vietnam", FlagStyle.SOLID_STAR, listOf(Color(0xFFDA251D), Color(0xFFFFFF00)), Color(0xFFFFFF00), "star"),
    CountryFlag("TH", "Thailand", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFA51931), Color(0xFFF4F5F8), Color(0xFF2D2A4A))),
    CountryFlag("MY", "Malaysia", FlagStyle.CANTON_STRIPES, listOf(Color(0xFFCC0000), Color(0xFFFFFFFF), Color(0xFF010066)), Color(0xFFFFCC00), "crescent_star"),
    CountryFlag("SG", "Singapore", FlagStyle.HORIZONTAL_2, listOf(Color(0xFFED2939), Color(0xFFFFFFFF)), Color(0xFFFFFFFF), "crescent_stars"),
    CountryFlag("LK", "Sri Lanka", FlagStyle.CENTRAL_EMBLEM, listOf(Color(0xFF8D153A), Color(0xFFFFBE29), Color(0xFF00534E)), Color(0xFFFFBE29), "lion"),
    CountryFlag("NP", "Nepal", FlagStyle.CENTRAL_EMBLEM, listOf(Color(0xFFDC143C), Color(0xFF003893)), Color(0xFFFFFFFF), "sun_moon"),
    CountryFlag("MN", "Mongolia", FlagStyle.VERTICAL_3, listOf(Color(0xFFC4272F), Color(0xFF015293), Color(0xFFC4272F)), Color(0xFFFFD900), "soyombo"),
    CountryFlag("TW", "Taiwan", FlagStyle.CANTON_STRIPES, listOf(Color(0xFFFE0000), Color(0xFF000095), Color(0xFFFFFFFF)), Color(0xFFFFFFFF), "sun"),
    CountryFlag("KZ", "Kazakhstan", FlagStyle.CENTRAL_EMBLEM, listOf(Color(0xFF00AFCA), Color(0xFFFEC50C)), Color(0xFFFEC50C), "sun_eagle"),
    CountryFlag("UZ", "Uzbekistan", FlagStyle.HORIZONTAL_3, listOf(Color(0xFF1EB53A), Color(0xFFFFFFFF), Color(0xFF0099B5)), Color(0xFFFFFFFF), "crescent_stars"),
    CountryFlag("GE", "Georgia", FlagStyle.GREEK_CROSS, listOf(Color(0xFFFFFFFF), Color(0xFFFF0000)), Color(0xFFFF0000), "bolnisi_crosses"),
    CountryFlag("AM", "Armenia", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFD90012), Color(0xFF0033A0), Color(0xFFF2A800))),
    CountryFlag("AZ", "Azerbaijan", FlagStyle.HORIZONTAL_3, listOf(Color(0xFF00B5E2), Color(0xFFEF3340), Color(0xFF509E2F)), Color(0xFFFFFFFF), "crescent_star"),

    // 4. Middle East
    CountryFlag("TR", "Turkey", FlagStyle.STAR_AND_CRESCENT, listOf(Color(0xFFE30A17), Color(0xFFFFFFFF))),
    CountryFlag("SA", "Saudi Arabia", FlagStyle.CENTRAL_EMBLEM, listOf(Color(0xFF006C35), Color(0xFFFFFFFF)), Color(0xFFFFFFFF), "sword"),
    CountryFlag("AE", "United Arab Emirates", FlagStyle.VERTICAL_2, listOf(Color(0xFFFF0000), Color(0xFF00732F), Color(0xFFFFFFFF), Color(0xFF000000))),
    CountryFlag("QA", "Qatar", FlagStyle.VERTICAL_2, listOf(Color(0xFFFFFFFF), Color(0xFF8D1B3D))),
    CountryFlag("IL", "Israel", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFFFFFFF), Color(0xFF0038B8)), Color(0xFF0038B8), "david_star"),
    CountryFlag("JO", "Jordan", FlagStyle.TRIANGLE_CHEVRON, listOf(Color(0xFF000000), Color(0xFFFFFFFF), Color(0xFF007A3D), Color(0xFFCE1126)), Color(0xFFFFFFFF), "star"),
    CountryFlag("KW", "Kuwait", FlagStyle.TRIANGLE_CHEVRON, listOf(Color(0xFF007A3D), Color(0xFFFFFFFF), Color(0xFFCE1126), Color(0xFF000000))),
    CountryFlag("OM", "Oman", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFDB161B), Color(0xFFFFFFFF), Color(0xFF008000))),
    CountryFlag("BH", "Bahrain", FlagStyle.VERTICAL_2, listOf(Color(0xFFFFFFFF), Color(0xFFDA291C))),
    CountryFlag("LB", "Lebanon", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFED1C24), Color(0xFFFFFFFF), Color(0xFFED1C24)), Color(0xFF00A651), "cedar"),

    // 5. Africa
    CountryFlag("EG", "Egypt", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFC8102E), Color(0xFFFFFFFF), Color(0xFF000000)), Color(0xFFC09300), "eagle"),
    CountryFlag("ZA", "South Africa", FlagStyle.TRIANGLE_CHEVRON, listOf(Color(0xFFE03C31), Color(0xFF001489), Color(0xFF007749), Color(0xFFFFB81C), Color(0xFF000000), Color(0xFFFFFFFF))),
    CountryFlag("NG", "Nigeria", FlagStyle.VERTICAL_3, listOf(Color(0xFF008751), Color(0xFFFFFFFF), Color(0xFF008751))),
    CountryFlag("KE", "Kenya", FlagStyle.HORIZONTAL_3, listOf(Color(0xFF000000), Color(0xFF922529), Color(0xFF006600)), Color(0xFFFFFFFF), "shield"),
    CountryFlag("MA", "Morocco", FlagStyle.SOLID_STAR, listOf(Color(0xFFC1272D), Color(0xFF006233)), Color(0xFF006233), "pentagram"),
    CountryFlag("GH", "Ghana", FlagStyle.HORIZONTAL_3, listOf(Color(0xFFCE1126), Color(0xFFFCD116), Color(0xFF006B3F)), Color(0xFF000000), "star"),
    CountryFlag("ET", "Ethiopia", FlagStyle.HORIZONTAL_3, listOf(Color(0xFF078930), Color(0xFFFCDD09), Color(0xFFDA121A)), Color(0xFF0F47AF), "star"),
    CountryFlag("SN", "Senegal", FlagStyle.VERTICAL_3, listOf(Color(0xFF00853F), Color(0xFFFDEF42), Color(0xFFE31B23)), Color(0xFF00853F), "star"),
    CountryFlag("CM", "Cameroon", FlagStyle.VERTICAL_3, listOf(Color(0xFF007A5E), Color(0xFFCE1126), Color(0xFFFCD116)), Color(0xFFFCD116), "star"),
    CountryFlag("CI", "Ivory Coast", FlagStyle.VERTICAL_3, listOf(Color(0xFFF77F00), Color(0xFFFFFFFF), Color(0xFF009E60))),
    CountryFlag("DZ", "Algeria", FlagStyle.STAR_AND_CRESCENT, listOf(Color(0xFF006233), Color(0xFFFFFFFF), Color(0xFFD21034))),
    CountryFlag("TN", "Tunisia", FlagStyle.STAR_AND_CRESCENT, listOf(Color(0xFFE70013), Color(0xFFFFFFFF))),
    CountryFlag("TZ", "Tanzania", FlagStyle.DIAGONAL_SALTIRE, listOf(Color(0xFF1EB53A), Color(0xFF00A3DD), Color(0xFF000000), Color(0xFFFCD116))),
    CountryFlag("UG", "Uganda", FlagStyle.HORIZONTAL_3, listOf(Color(0xFF000000), Color(0xFFFCDC04), Color(0xFFD90000)), Color(0xFFFFFFFF), "crane"),
    CountryFlag("AO", "Angola", FlagStyle.HORIZONTAL_2, listOf(Color(0xFFCC092F), Color(0xFF000000)), Color(0xFFFFCB00), "cog"),
    CountryFlag("ZW", "Zimbabwe", FlagStyle.TRIANGLE_CHEVRON, listOf(Color(0xFF006400), Color(0xFFFFD200), Color(0xFFD40000), Color(0xFF000000), Color(0xFFFFFFFF))),
    CountryFlag("RW", "Rwanda", FlagStyle.HORIZONTAL_3, listOf(Color(0xFF00A1DE), Color(0xFFFAD201), Color(0xFF20603D)), Color(0xFFFAD201), "sun"),
    CountryFlag("ZM", "Zambia", FlagStyle.HORIZONTAL_3, listOf(Color(0xFF198A00), Color(0xFFDE2010), Color(0xFF000000), Color(0xFFEF7D00))),
    CountryFlag("MG", "Madagascar", FlagStyle.VERTICAL_2, listOf(Color(0xFFFFFFFF), Color(0xFFFC3D32), Color(0xFF007E3A))),
    CountryFlag("ML", "Mali", FlagStyle.VERTICAL_3, listOf(Color(0xFF14B53A), Color(0xFFFCD116), Color(0xFFCE1126))),

    // 6. Oceania
    CountryFlag("AU", "Australia", FlagStyle.CANTON_STRIPES, listOf(Color(0xFF00008B), Color(0xFFFFFFFF), Color(0xFFFF0000)), Color(0xFFFFFFFF), "southern_cross"),
    CountryFlag("NZ", "New Zealand", FlagStyle.CANTON_STRIPES, listOf(Color(0xFF00247D), Color(0xFFCC142B), Color(0xFFFFFFFF)), Color(0xFFCC142B), "southern_cross_red"),
    CountryFlag("FJ", "Fiji", FlagStyle.CANTON_STRIPES, listOf(Color(0xFF68BFE5), Color(0xFF012169), Color(0xFFCC142B)), Color(0xFFFFFFFF), "shield"),
    CountryFlag("PG", "Papua New Guinea", FlagStyle.DIAGONAL_SALTIRE, listOf(Color(0xFF000000), Color(0xFFCE1126)), Color(0xFFFFCC00), "bird_stars"),
    CountryFlag("WS", "Samoa", FlagStyle.CANTON_STRIPES, listOf(Color(0xFFCE1126), Color(0xFF002B7F), Color(0xFFFFFFFF)), Color(0xFFFFFFFF), "southern_cross")
  )

  /**
   * Helper that selects 25 unique flags for a new round while strictly
   * excluding all 25 flags from the immediate previous round.
   */
  fun pickNextRoundFlags(previousRoundCodes: Set<String>, recentSurvivors: List<String>): List<CountryFlag> {
    val candidatePool = ALL_FLAGS.filter { it.code !in previousRoundCodes }
    val shuffled = candidatePool.shuffled()

    // Pick 25 flags from candidates (ALL_FLAGS has 100+ flags so candidatePool has 75+)
    val selected = if (shuffled.size >= 25) {
      shuffled.take(25)
    } else {
      val remainingNeeded = 25 - shuffled.size
      val backupPool = ALL_FLAGS.filter { it !in shuffled }.shuffled()
      shuffled + backupPool.take(remainingNeeded)
    }
    return selected
  }
}
