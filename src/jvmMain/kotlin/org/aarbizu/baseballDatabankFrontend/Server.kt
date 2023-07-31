package org.aarbizu.baseballDatabankFrontend

import com.google.common.base.Stopwatch
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticFiles
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.aarbizu.baseballDatabankFrontend.config.AppConfig
import org.aarbizu.baseballDatabankFrontend.db.DataLoader
import org.aarbizu.baseballDatabankFrontend.query.NoneQueryEngine
import org.aarbizu.baseballDatabankFrontend.query.PreloadedResults
import org.aarbizu.baseballDatabankFrontend.query.QueryEngine
import org.aarbizu.baseballDatabankFrontend.query.playerNamesSorted
import org.aarbizu.baseballDatabankFrontend.query.preloadQueries
import org.aarbizu.baseballDatabankFrontend.query.toJsonArray
import org.aarbizu.baseballDatabankFrontend.retrosheet.SeasonProgress
import org.aarbizu.baseballDatabankFrontend.retrosheet.TeamInfo
import org.h2.tools.Server
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files.readString
import java.nio.file.Paths
import kotlin.math.log

private const val DEFAULT_HTML_DOC = "src/commonMain/resources/index.html"
val defaultHtmlText: String = readString(Paths.get(DEFAULT_HTML_DOC))

data class QueryEngineService(var engine: QueryEngine)

val queryService = QueryEngineService(NoneQueryEngine)

class Server(private val config: AppConfig) {

    fun start() {
        /* process retrosheet data */
        initializeRetrosheet()
        /* init db */
        initializeDb(config)
        queryService.engine = QueryEngine(config.db)
        PreloadedResults.preloads = preloadQueries(queryService.engine)
        /* this needs to be last since it starts the server loop */
        startBackend(config)
    }

    private fun initializeRetrosheet() {
        /* initialize historical team info */
        val log = LoggerFactory.getLogger(this.javaClass)
        val timer = Stopwatch.createStarted()
        TeamInfo().initializeMap()
        log.info("initialized team info map: ${TeamInfo.teamInfoMap.size} mappings, $timer")
    }

    private fun initializeDb(config: AppConfig) {
        val log = LoggerFactory.getLogger(this.javaClass)
        log.info("Initializing database...")
        val timer = Stopwatch.createStarted()

        // load csv files into the db
        val dataLoader = DataLoader(config.db)
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
        embeddedServer(Netty, config.port, module = Application::databankBackend)
            .start(wait = true)
    }
}

fun Application.databankBackend() {
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

    val seasonProgress = SeasonProgress()

    routing {
        //                trace {
        // LoggerFactory.getLogger(this.javaClass).info(it.buildText()) }
        post(PLAYER_NAME_LENGTH) {
            val param = call.receive<PlayerNameLengthParam>()
            call.respond(queryService.engine.playerNamesByLength(param))
        }

        post(PLAYER_NAME) {
            val param = call.receive<PlayerNameSearchParam>()
            call.respond(queryService.engine.playerNameSearch(param))
        }

        post(MIN_MAX_VALUES) { call.respond(PreloadedResults.preloads.minMaxValues) }

        post(NAMES_SORTED_BY_LENGTH) {
            val param = call.receive<NamesSortedByLengthParam>()
            call.respond(playerNamesSorted(param))
        }

        post(OFFENSE_STATS) { call.respond(toJsonArray(PreloadedResults.preloads.offenseStats.statNames)) }

        post(PITCHING_STATS) { call.respond(toJsonArray(PreloadedResults.preloads.pitchingStats.statNames)) }

        post(ALL_TIME_HITTING) {
            val param = call.receive<StatParam>()
            call.respond(queryService.engine.offenseStatLeaders(param))
        }

        post(ALL_TIME_PITCHING) {
            val param = call.receive<StatParam>()
            call.respond(queryService.engine.pitchingStatLeaders(param))
        }

        post(SEASON_DAILY_STANDINGS) {
            val param = call.receive<SeasonDailyStandingsParam>()
            call.respond(seasonProgress.plotDayByDayStandings(param.year, param.division))
        }

        route("/") {
            route("{...}") {
                get { call.respondText(defaultHtmlText, ContentType.Text.Html) }
            }
        }

        staticFiles("/static", File(""))

        staticResources("/static", "")
    }
}
