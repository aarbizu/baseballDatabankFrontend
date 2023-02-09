package org.aarbizu.baseballDatabankFrontend

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.browser.window
import kotlinx.serialization.json.Json

val endpoint = window.location.origin

val jsonClient = HttpClient {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
            },
        )
    }
}

suspend fun queryPlayerNameLength(lengthParam: PlayerNameLengthParam): List<SimplePlayerRecord> {
    return jsonClient
        .post("$endpoint/$PLAYER_NAME_LENGTH") {
            contentType(ContentType.Application.Json)
            setBody(lengthParam)
        }
        .body()
}

suspend fun queryPlayerName(nameParam: PlayerNameSearchParam): List<SimplePlayerRecord> {
    return jsonClient
        .post("$endpoint/$PLAYER_NAME") {
            contentType(ContentType.Application.Json)
            setBody(nameParam)
        }
        .body()
}

suspend fun getMinMaxNameLengths(): MinMaxValues {
    return jsonClient
        .post("$endpoint/$MIN_MAX_VALUES") { contentType(ContentType.Application.Json) }
        .body()
}

suspend fun getSortedNames(sortedNameParams: NamesSortedByLengthParam): List<SimplePlayerRecord> {
    return jsonClient
        .post("$endpoint/$NAMES_SORTED_BY_LENGTH") {
            contentType(ContentType.Application.Json)
            setBody(sortedNameParams)
        }
        .body()
}

suspend fun getOffenseStatNames(): OffenseStats {
    return jsonClient
        .post("$endpoint/$OFFENSE_STATS") { contentType(ContentType.Application.Json) }
        .body()
}

suspend fun getPitchingStatNames(): PitchingStats {
    return jsonClient
        .post("$endpoint/$PITCHING_STATS") { contentType(ContentType.Application.Json) }
        .body()
}

suspend fun getOffenseCareerStats(offenseStatParam: StatParam): List<PlayerCareerStatRecord> {
    return jsonClient
        .post("$endpoint/$ALL_TIME_HITTING") {
            contentType(ContentType.Application.Json)
            setBody(offenseStatParam)
        }
        .body()
}

suspend fun getPitchingCareerStats(pitchingStatParam: StatParam): List<PlayerCareerStatRecord> {
    return jsonClient
        .post("$endpoint/$ALL_TIME_PITCHING") {
            contentType(ContentType.Application.Json)
            setBody(pitchingStatParam)
        }
        .body()
}
