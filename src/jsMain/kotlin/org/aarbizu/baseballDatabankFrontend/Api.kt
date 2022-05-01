package org.aarbizu.baseballDatabankFrontend

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
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
            }
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
