package org.aarbizu.baseballDatabankFrontend

import com.google.common.base.Stopwatch
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.CORS
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.aarbizu.baseballDatabankFrontend.config.AppConfig
import org.aarbizu.baseballDatabankFrontend.db.DataLoader
import org.aarbizu.baseballDatabankFrontend.query.QueryEngine
import org.h2.tools.Server
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.nio.file.Files.readString
import java.nio.file.Paths

private const val DEFAULT_HTML_DOC = "src/commonMain/resources/index.html"

class Server(private val config: AppConfig) {
    private lateinit var queries: QueryEngine
    private lateinit var defaultHtmlText: String

    fun start() {
        initializeDb(config)
        queries = QueryEngine(config.db)
        defaultHtmlText = readString(Paths.get(DEFAULT_HTML_DOC))

        /* this needs to be last since it starts the server loop */
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

        val useDebugServer = System.getenv("DB_DEBUG")?.toBoolean() ?: false

        if (useDebugServer) {
            Server.createTcpServer("-tcp", "-tcpPort", "9999").start()
            log.info("h2 tcp server started on port 9999")
        }
    }

    private fun startBackend(config: AppConfig) {
        embeddedServer(Netty, config.port) {
            install(CallLogging) { level = Level.DEBUG }
            install(ContentNegotiation) { json() }
            install(CORS) {
                allowMethod(HttpMethod.Options)
                allowMethod(HttpMethod.Get)
                allowMethod(HttpMethod.Post)
                allowHeader(HttpHeaders.AccessControlAllowOrigin)
                allowHeader(HttpHeaders.Authorization)
                allowHeader(HttpHeaders.ContentType)
                allowNonSimpleContentTypes = true
                allowSameOrigin = true
                allowCredentials = true
                anyHost()
            }
            install(Compression) { gzip() }

            routing {
                trace { LoggerFactory.getLogger(this.javaClass).info(it.buildText()) }
                post(PLAYER_NAME_LENGTH) {
                    val param = call.receive<PlayerNameLengthParam>()
                    call.respond(queries.playerNamesByLength(param.nameLength))
                }

                post(PLAYER_NAME) {
                    val param = call.receive<PlayerNameSearchParam>()
                    call.respond(queries.playerNameSearch(param))
                }

                route("/") {
                    route("{...}") {
                        get { call.respondText(defaultHtmlText, ContentType.Text.Html) }
                    }
                }

                static("/static") { resources("") }
            }
        }
            .start(wait = true)
    }
}
