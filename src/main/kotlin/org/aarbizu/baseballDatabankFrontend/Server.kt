package org.aarbizu.baseballDatabankFrontend

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.timeout
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import java.time.Duration
import kweb.Kweb
import kweb.button
import kweb.h1
import kweb.input
import kweb.new
import kweb.span
import kweb.state.KVar

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(10)
        timeout = Duration.ofSeconds(30)
    }
    install(Routing) {
        get("/ktor") {
            call.respondText("default route, yo. watch me, Ktor!", ContentType.Text.Html)
        }
    }
    install(Kweb) {
        buildPage = {
            doc.body.new {
                val greeting = url.map { it.removePrefix("/") }.map { "Hello " + if (it.isNotBlank()) it else "World" }

                val next = KVar("")

                h1().text(greeting)
                span().text("Where to next?")
                input().setValue(next)
                button().text("Go!").on.click {
                    url.value = next.value
                }
            }
        }
    }
}
class Server {
    fun start() {
        embeddedServer(Netty, 8080, watchPaths = listOf("ServerKt"), module = Application::module).start()
    }
}
