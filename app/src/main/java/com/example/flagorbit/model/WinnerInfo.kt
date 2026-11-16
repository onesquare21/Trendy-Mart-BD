package com.example.flagorbit.model

/**
 * Data holder for the dynamic round winner announcement.
 */
data class WinnerInfo(
  val country: CountryFlag,
  val flagEmoji: String,
  val announcementText: String,
  val roundNumber: Int
)
