package org.aarbizu.baseballDatabankFrontend.routes

import io.ktor.http.Parameters
import kweb.ElementCreator
import kweb.a
import kweb.button
import kweb.div
import kweb.h1
import kweb.i
import kweb.new
import kweb.plugins.fomanticUI.fomantic
import kweb.state.KVar

class DefaultRoute(private val crumbs: MutableList<Crumb>) : RouteHandler {
    override fun getCrumb(parameters: Parameters) = Crumb.empty

    override fun injectCrumbs() = crumbs

    override fun handleRoute(ec: ElementCreator<*>, parameters: Parameters) {
        with(ec) {
            div(fomantic.ui.center.aligned.container).new {
                div(fomantic.ui.hidden.divider)
                div(fomantic.content).new {
                    h1(fomantic.ui.icon.header).new {
                        i(baseballGlyphStyle)
                        div(fomantic.content).text("Baseball Databank").new {
                            div(subHeaderStyle).text("Historical baseball database derived from: ").new {
                                a(href = "https://github.com/chadwickbureau/baseballdatabank")
                                    .text("The Chadwick Baseball Databank").tag
                            }
                        }
                    }
                    div(fomantic.content).new {
                        a(href = "http://chadwick-bureau.com/")
                            .text("Visit the Chadwick Baseball Bureau")
                    }
                    div(fomantic.ui.divider)
                    button(massiveButtonStyle).text("Get Started").on.click {
                        browser.url.value = "/q/begin"
                    }.new {
                        i(fomantic.ui.icon.right.arrow)
                    }
                }
            }
        }
    }

    override suspend fun updateUrl(url: KVar<String>, inputs: Map<String, String>) = Unit // no-op here
}
