package org.aarbizu.baseballDatabankFrontend

import com.google.common.base.Stopwatch
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
import kweb.Kweb
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.respondKwebRender
import kweb.route
import org.aarbizu.baseballDatabankFrontend.db.DB
import org.aarbizu.baseballDatabankFrontend.routes.handleRoutes
import org.slf4j.LoggerFactory
import java.time.Duration

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
                        handleRoutes()
                    }
                }
            }
        }
    }
}

class Server {
    fun start() {

        val log = LoggerFactory.getLogger(this.javaClass)
        log.info("Iniitializing database...")
        val timer = Stopwatch.createStarted()
        DB.init()
        log.info("database init complete in {}", timer.toString())

        val port = System.getenv("PORT")?.toInt() ?: 8080
        embeddedServer(
            Netty,
            port,
            module = Application::module
        ).start()
    }
}
