package org.aarbizu.baseballDatabankFrontend

import kotlinx.serialization.Serializable

interface BaseballRecord

@Serializable
class SimplePlayerRecord(
    val name: String,
    val born: String,
    val debut: String,
    val finalGame: String,
    val playerId: String,
    val bbrefId: String,
) : BaseballRecord

@Serializable
class PlayerSeasonStatRecord(
    val name: String,
    val year: String,
    val statName: String,
) : BaseballRecord

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
