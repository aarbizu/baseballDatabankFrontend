package org.aarbizu.baseballDatabankFrontend

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.browser.window

val endpoint = window.location.origin

val jsonClient = HttpClient { install(JsonFeature) { serializer = KotlinxSerializer() } }

suspend fun queryPlayerNameLength(lengthParam: PlayerNameLengthParam): List<SimplePlayerRecord> {
    return jsonClient.post("$endpoint/player-name-length") {
        contentType(ContentType.Application.Json)
        body = lengthParam
    }
}
