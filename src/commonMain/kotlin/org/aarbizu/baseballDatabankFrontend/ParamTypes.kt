package org.aarbizu.baseballDatabankFrontend

import kotlinx.serialization.Serializable

/** Parameter Types for talking to the backend service */
@Serializable class PlayerNameLengthParam(val nameLength: String, val nameOption: String)

@Serializable
class PlayerNameSearchParam(
    val nameSearchString: String,
    val matchFirstName: Boolean = false,
    val matchLastName: Boolean = false,
    val caseSensitive: Boolean = false
)

@Serializable
class NamesSortedByLengthParam(val type: String, val descending: String, val topN: String)

@Serializable class StatParam(val stat: String)
