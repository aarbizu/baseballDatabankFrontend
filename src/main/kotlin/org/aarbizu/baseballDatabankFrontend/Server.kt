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
import kweb.ElementCreator
import kweb.Kweb
import kweb.button
import kweb.li
import kweb.new
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.respondKwebRender
import kweb.route
import kweb.toInt
import kweb.ul

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
            get("/{visitedUrl...}") {
                call.respondKwebRender {
                    getRouteReceiver()
                }
            }
        }
    }
}

private fun ElementCreator<*>.getRouteReceiver() {
    route {
        path("/") {
            browser.url.value = "/number/1"
        }
        path("/number/{num}") { params ->
            var ulist = ul(fomantic.ui.bulleted.list).new {
                li(fomantic.ui.bulleted).text("num = ${ params.getValue("num").value }")
            }
            val num = params.getValue("num").toInt()
            button(fomantic.ui.button).text(num.map { "Number $it" }).on.click {
                num.value++
                ulist.innerHTML("num = ${ params.getValue("num").value }")
            }
        }
    }
}

class Server {
    fun start() {
        embeddedServer(Netty,
                8080,
                watchPaths = listOf("baseballDatabankFrontend"),
                module = Application::module
        ).start()
    }
}
