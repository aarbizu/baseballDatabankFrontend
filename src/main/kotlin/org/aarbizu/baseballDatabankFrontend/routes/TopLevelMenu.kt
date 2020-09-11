package org.aarbizu.baseballDatabankFrontend.routes

import io.ktor.http.Parameters
import kweb.ElementCreator
import kweb.a
import kweb.div
import kweb.id
import kweb.new
import kweb.plugins.fomanticUI.fomantic
import kweb.state.KVar

class TopLevelMenu(private val crumbs: MutableList<Crumb>) : RouteHandler {

    override fun getCrumb(parameters: Parameters) = Crumb("Begin", TOP_LEVEL_MENU_LOCATION)

    override fun injectCrumbs() = crumbs

    override suspend fun updateUrl(url: KVar<String>, inputs: Map<String, String>) = Unit // no-op here

    override fun handleRoute(ec: ElementCreator<*>, parameters: Parameters) {
        with(ec) {
            div(fomantic.ui.hidden.divider)
            div(fomantic.ui.container).new {
                div(fomantic.ui.three.item.menu).new {
                    a(fomantic.ui.item).text("Player Name By Length").on.click {
                        browser.url.value = "/q/$playerNameLength/"
                    }
                    a(fomantic.ui.item).text("Player Name Search").on.click {
                        browser.url.value = "/q/$playerLastNameSearchQuery/"
                    }
                    a(fomantic.ui.item).text("Player Name Regex Search").on.click {
                        browser.url.value = "/q/$playerNameRegex/"
                    }
                }
            }
            div(fomantic.ui.hidden.divider)
            div(fomantic.ui.container.id("errors"))
        }
    }
}
