package org.aarbizu.baseballDatabankFrontend

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.timeout
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import java.time.Duration
import kweb.Kweb
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.respondKwebRender
import kweb.route
import org.aarbizu.baseballDatabankFrontend.routes.dispatch

const val uriPrefix = "loc"

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(30)
    }
    install(Kweb) {
        plugins = listOf(fomanticUIPlugin)
        routing {
            // trace { logger.info(it.buildText()) }
            get("{...}") {
                val parameters = call.request.queryParameters
                call.respondKwebRender {
                    route {
                        dispatch(parameters)
                    }
                }
            }
        }
    }
}

class Server {
    fun start() {
        val port = System.getenv("PORT")?.toInt() ?: 8080
        embeddedServer(Netty,
            port,
            module = Application::module
        ).start()
    }
}
