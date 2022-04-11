package org.aarbizu.baseballDatabankFrontend

import com.google.common.base.Stopwatch
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.gzip
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.aarbizu.baseballDatabankFrontend.config.AppConfig
import org.aarbizu.baseballDatabankFrontend.db.DataLoader
import org.slf4j.LoggerFactory

//fun Application.module() {
//    install(DefaultHeaders)
//    install(Compression)
//    install(WebSockets) {
//        pingPeriod = Duration.ofSeconds(15)
//        timeout = Duration.ofSeconds(30)
//    }
//    install(Kweb) {
//        plugins = listOf(fomanticUIPlugin)
//        kwebConfig = DatabankKwebConfig()
//        routing {
//            trace { LoggerFactory.getLogger(this.javaClass).info(it.buildText()) }
//            get("/{visitedUrl...}") {
//                call.respondKwebRender {
//                    route {
//                        handleRoutes()
//                    }
//                }
//            }
//        }
//    }
//}

class Server(private val config: AppConfig) {
    fun start() {
        initializeDb(config)
        startBackend(config)
    }

    private fun initializeDb(config: AppConfig) {
        val log = LoggerFactory.getLogger(this.javaClass)
        log.info("Initializing database...")
        val timer = Stopwatch.createStarted()

        // load csv files into the db
        val dataLoader = DataLoader(config.db, config.csvHome)
        dataLoader.loadAllFiles()
        dataLoader.buildIndexes()
        config.db.stats()
        log.info("database init complete in {}", timer.toString())
    }

    private fun startBackend(config: AppConfig) {
        embeddedServer(
            Netty,
            config.port
        ) {
            install(ContentNegotiation) {
                json()
            }
            install(CORS) {
                method(HttpMethod.Get)
                method(HttpMethod.Post)
                method(HttpMethod.Delete)
                anyHost()
            }
            install(Compression) {
                gzip()
            }

            routing {
                trace { LoggerFactory.getLogger(this.javaClass).info(it.buildText()) }
                get("/") {
                    call.respondText(
                        this::class.java.classLoader.getResource("index.html")!!.readText(),
                        ContentType.Text.Html
                    )
                }
                static("/") {
                    resources("")
                }
            }
        }.start(wait = true)
    }
}
