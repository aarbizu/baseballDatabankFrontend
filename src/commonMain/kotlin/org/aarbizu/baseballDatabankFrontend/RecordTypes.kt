package org.aarbizu.baseballDatabankFrontend

import kotlinx.serialization.Serializable

@Serializable
sealed interface BaseballRecord

@Serializable
class SimplePlayerRecord(
    val first: String,
    val last: String,
    val given: String,
    val name: String,
    val born: String,
    val debut: String,
    val finalGame: String,
    val playerId: String,
    val bbrefId: String,
    val playerMgr: String,
) : BaseballRecord

@Serializable
class PlayerSeasonStatRecord(val name: String, val year: String, val statName: String) :
    BaseballRecord

@Serializable
class MinMaxValues(
    val minFirstName: String,
    val maxFirstName: String,
    val minLastName: String,
    val maxLastName: String,
    val minFirstAndLastName: String,
    val maxFirstAndLastName: String,
    val minFullName: String,
    val maxFullName: String,
) : BaseballRecord

@Serializable class OffenseStats(val statNames: List<String>) : BaseballRecord

@Serializable class PitchingStats(val statNames: List<String>) : BaseballRecord

@Serializable
class PlayerCareerStatRecord(val id: String, val name: String, val stat: String) : BaseballRecord
