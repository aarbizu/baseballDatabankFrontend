package org.aarbizu.baseballDatabankFrontend

import kotlinx.serialization.Serializable

/** Parameter Types for talking to the backend service */
@Serializable class PlayerNameLengthParam(val nameLength: String, val nameOption: String)

@Serializable
class PlayerNameSearchParam(
    val nameSearchString: String,
    val matchFirstName: Boolean = false,
    val matchLastName: Boolean = false,
    val caseSensitive: Boolean = false,
)

@Serializable
class NamesSortedByLengthParam(val type: String, val descending: String, val topN: String)

@Serializable class OffenseStatParam(val stat: String)

private const val bbrefUri = "https://www.baseball-reference.com"
private const val bbrefSuffix = ".shtml"

fun decorateBbrefId(bbrefid: String, playerMgr: String): String {
    return if (playerMgr == "1") {
        "$bbrefUri/managers/$bbrefid$bbrefSuffix"
    } else {
        "$bbrefUri/players/${bbrefid[0]}/$bbrefid$bbrefSuffix"
    }
}
