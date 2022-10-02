package org.aarbizu.baseballDatabankFrontend.query

import kotlinx.serialization.Serializable

@Serializable
enum class OffenseStatsNames {
    G,
    AB,
    R,
    H,
    DOUBLE,
    TRIPLE,
    HR,
    RBI,
    SB,
    CS,
    BB,
    SO,
    IBB,
    HBP,
    SH,
    SF,
    GIDP
}