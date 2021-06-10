package org.aarbizu.baseballDatabankFrontend.routes

import io.ktor.http.Parameters
import kweb.ElementCreator
import kweb.InputElement
import kweb.InputType
import kweb.WebBrowser
import kweb.button
import kweb.div
import kweb.i
import kweb.id
import kweb.input
import kweb.label
import kweb.new
import kweb.plugins.fomanticUI.fomantic
import kweb.state.KVar
import org.aarbizu.baseballDatabankFrontend.db.QueryEngine
import org.aarbizu.baseballDatabankFrontend.records.PaginatedRecords

const val playerLastNameSearchQuery = "player-name"
const val pPlayerLastNameParam = "last-name"

class SearchByPlayerLastName(private val crumbs: MutableList<Crumb>, private val queryEngine: QueryEngine) : RouteHandler {
    private val outputElementId = "names"

    override fun handleRoute(ec: ElementCreator<*>, parameters: Parameters) {
        with(ec) {
            div(fomantic.ui.hidden.divider)
            div(fomantic.ui.container).new {
                generatePlayerNameSearchForm(queryEngine)
                debugParamsElement(parameters)
            }

            handleQueryStringIfPresent(parameters, browser)

            div(fomantic.ui.hidden.divider)
            div(fomantic.ui.container.id("errors"))
        }
    }

    private fun handleQueryStringIfPresent(parameters: Parameters, browser: WebBrowser) {
        if (parameters[pPlayerLastNameParam]?.isNotEmpty()!!) {
            val lastName = parameters[pPlayerLastNameParam]!!
            val outputEl = browser.doc.getElementById(outputElementId)
            PaginatedRecords(queryEngine.playerNameSearch(lastName), outputEl).renderTable()
        }
    }

    private fun ElementCreator<*>.generatePlayerNameSearchForm(queries: QueryEngine) {
        val output = KVar("")
        var nameFragmentInput: InputElement? = null

        div(fomantic.ui.form).new {
            div(fomantic.fields).new {
                div(fomantic.six.wide.inline.field).new {
                    label().text("Last Name")
                    nameFragmentInput = input(
                        type = InputType.text,
                        name = pPlayerLastNameParam,
                        initialValue = "",
                        size = 32,
                        placeholder = """ Surname, e.g."Bonds" """
                    )
                    nameFragmentInput!!.on.keypress { ke ->
                        if (ke.code == "Enter") {
                            handleInput(
                                listOf(nameFragmentInput!!),
                                browser.doc.getElementById(outputElementId),
                                browser.url,
                                browser
                            ) { inputs ->
                                queries.playerNameSearch("%${inputs[0].lowercase()}%")
                            }
                        }
                    }
                }

                button(fieldButtonStyle).text("Search").on.click {
                    handleInput(
                        listOf(nameFragmentInput!!),
                        browser.doc.getElementById(outputElementId),
                        browser.url,
                        browser
                    ) { inputs ->
                        queries.playerNameSearch("%${inputs[0].lowercase()}%")
                    }
                }.new {
                    i(baseballGlyphStyle)
                }
            }
        }
        div(fomantic.ui.hidden.divider)
        div(fomantic.content.id(outputElementId)).text(output)
    }

    override suspend fun updateUrl(url: KVar<String>, inputs: Map<String, String>) {
        url.value = "/q/$playerLastNameSearchQuery/?$pPlayerLastNameParam=${inputs[pPlayerLastNameParam]}"
    }

    override fun getCrumb(parameters: Parameters) =
        Crumb("Last Name", "/q/$playerLastNameSearchQuery/?${parameters[playerLastNameSearchQuery]}")

    override fun injectCrumbs() = crumbs
}
