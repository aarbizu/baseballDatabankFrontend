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
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.aarbizu.baseballDatabankFrontend.config.AppConfig
import org.aarbizu.baseballDatabankFrontend.db.DataLoader
import org.aarbizu.baseballDatabankFrontend.query.QueryEngine
import org.slf4j.LoggerFactory

class Server(private val config: AppConfig) {
    private lateinit var queries: QueryEngine

    fun start() {
        initializeDb(config)
        queries = QueryEngine(config.db)

        // this has to come last, since it starts the server and doesn't exit until the app stops
        // and exists
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
        embeddedServer(Netty, config.port) {
            install(ContentNegotiation) { json() }
            install(CORS) {
                method(HttpMethod.Get)
                method(HttpMethod.Post)
                anyHost()
            }
            install(Compression) { gzip() }

            routing {
                //                trace {
                // LoggerFactory.getLogger(this.javaClass).info(it.buildText()) }
                route("player-name-length") {
                    post {
                        val param = call.receive<PlayerNameLengthParam>()
                        call.respond(queries.playerNamesByLength(param.nameLength))
                    }
                }

                route("player-lastname-search") {
                    post {
                        val param = call.receive<PlayerNameSearchParam>()
                        call.respond(queries.playerNameSearch(param.nameSearchString))
                    }
                }

                route("player-lastname-regex-search") {
                    post {
                        val param = call.receive<PlayerNameSearchParam>()
                        call.respond(
                            queries.playerNameRegexSearch(
                                param.nameSearchString,
                                param.matchFirstName,
                                param.matchLastName,
                                param.caseSensitive
                            )
                        )
                    }
                }

                get("/") {
                    call.respondText(
                        this::class.java.classLoader.getResource("index.html")!!.readText(),
                        ContentType.Text.Html
                    )
                }
                static("/") { resources("") }
            }
        }
            .start(wait = true)
    }
}
