package org.aarbizu.baseballDatabankFrontend

import com.google.common.base.Stopwatch
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.Compression
import io.ktor.features.DefaultHeaders
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.timeout
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import kweb.Kweb
import kweb.config.KwebDefaultConfiguration
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.respondKwebRender
import kweb.route
import org.aarbizu.baseballDatabankFrontend.db.DB
import org.aarbizu.baseballDatabankFrontend.db.DBProvider
import org.aarbizu.baseballDatabankFrontend.db.DataLoader
import org.aarbizu.baseballDatabankFrontend.routes.handleRoutes
import org.slf4j.LoggerFactory
import java.time.Duration

fun Application.module() {
    install(DefaultHeaders)
    install(Compression)
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(30)
    }
    install(Kweb) {
        plugins = listOf(fomanticUIPlugin)
        kwebConfig = DatabankKwebConfig()
        routing {
            trace { LoggerFactory.getLogger(this.javaClass).info(it.buildText()) }
            get("/{visitedUrl...}") {
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
        initializeDb(DB)
        DB.stats()
        log.info("database init complete in {}", timer.toString())

        val port = System.getenv("PORT")?.toInt() ?: 8080
        embeddedServer(
            Netty,
            port,
            module = Application::module
        ).start()
    }

    private fun initializeDb(db: DBProvider) {
        val dataLoader = DataLoader(db)
        dataLoader.loadAllFiles()
        dataLoader.buildIndexes()
    }
}

/**
 * Dislike the banners that pop-up randomly (?) so turn them off
 */
class DatabankKwebConfig : KwebDefaultConfiguration() {
    override val clientOfflineBannerStyle: String
        get() = ""
    override val clientOfflineBannerTextTemplate: String
        get() = ""
}
