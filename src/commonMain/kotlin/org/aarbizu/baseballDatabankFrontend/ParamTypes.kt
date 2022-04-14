package org.aarbizu.baseballDatabankFrontend

import kotlinx.serialization.Serializable

/** Parameter Types for talking to the backend service */
@Serializable class PlayerNameLengthParam(val nameLength: String)

@Serializable
class PlayerNameSearchParam(
    val nameSearchString: String,
    val matchFirstName: Boolean = false,
    val matchLastName: Boolean = false,
    val caseSensitive: Boolean = false,
)

// TODO stashing these here for later front-end use
private const val bbrefUri = "https://www.baseball-reference.com/players"
private const val bbrefSuffix = ".shtml"

fun decorateBbrefId(bbrefid: String): String {
    return "$bbrefUri/${bbrefid[0]}/$bbrefid$bbrefSuffix"
}
