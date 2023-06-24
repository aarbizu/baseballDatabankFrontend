package org.aarbizu.baseballDatabankFrontend

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val endpoint = window.location.origin

val serializers = SerializersModule {
    polymorphic(BaseballRecord::class) {
        subclass(SimplePlayerRecord::class)
        subclass(PlayerCareerStatRecord::class)
    }
}

val jsonClient = HttpClient {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                serializersModule = serializers
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

suspend fun getMinMaxNameLengths(): String {
    return jsonClient
        .post("$endpoint/$MIN_MAX_VALUES") { contentType(ContentType.Application.Json) }
        .bodyAsText()
}

suspend fun getSortedNames(sortedNameParams: NamesSortedByLengthParam): List<SimplePlayerRecord> {
    return jsonClient
        .post("$endpoint/$NAMES_SORTED_BY_LENGTH") {
            contentType(ContentType.Application.Json)
            setBody(sortedNameParams)
        }
        .body()
}

suspend fun getOffenseStatNames(): String {
    return jsonClient
        .post("$endpoint/$OFFENSE_STATS") { contentType(ContentType.Application.Json) }
        .bodyAsText()
}

suspend fun getPitchingStatNames(): String {
    return jsonClient
        .post("$endpoint/$PITCHING_STATS") { contentType(ContentType.Application.Json) }
        .bodyAsText()
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
