package org.aarbizu.baseballDatabankFrontend.routes

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

const val playerNameLength = "player-name-by-len"
const val pPlayerNameLength = "name-length"

class SearchByNameLength(private val crumbs: MutableList<Crumb>, private val queryEngine: QueryEngine) : RouteHandler {
    private val outputFieldId = "output"

    override fun getCrumb() =
        Crumb("Name Length Search", "/q/$playerNameLength")

    override fun injectCrumbs() = crumbs

    override fun handleRoute(ec: ElementCreator<*>, params: Map<String, KVar<String>>) {
        with(ec) {
            div(fomantic.ui.hidden.divider)
            div(fomantic.ui.container).new {
                generatePlayerNameLengthForm(queryEngine, params["lengthParam"]?.value)
                debugParamsElement(params)
            }

            params["lengthParam"]?.value?.let { handleQueryStringIfPresent(it, browser) }

            div(fomantic.ui.hidden.divider)
            div(fomantic.ui.container.id("errors"))
        }
    }

    private fun handleQueryStringIfPresent(lengthParam: String, browser: WebBrowser) {
        if (lengthParam.isNotEmpty()) {
            val outputDiv = browser.doc.getElementById(outputFieldId)
            PaginatedRecords(
                queryEngine.playerNamesByLength(
                    lengthParam
                ), outputDiv
            ).renderTable()
            browser.url.value = "/q/$playerNameLength/$lengthParam"
        }
    }

    private fun ElementCreator<*>.generatePlayerNameLengthForm(queries: QueryEngine, lengthValue: String?) {
        val output = KVar("")
        var nameLengthInput: InputElement? = null

        div(fomantic.ui.form).new {
            div(fomantic.fields).new {
                div(fomantic.inline.field).new {
                    label().text("Name Length")
                    nameLengthInput = input(
                        type = InputType.text,
                        name = pPlayerNameLength,
                        initialValue = lengthValue ?: "",
                        size = 15,
                        placeholder = "14"
                    )
                    nameLengthInput?.on?.keypress { ke ->
                        if (ke.code == "Enter") {
                            handleInput(
                                listOf(nameLengthInput!!),
                                browser.doc.getElementById(outputFieldId),
                                browser.url,
                                browser
                            ) { inputs ->
                                queries.playerNamesByLength(inputs[0])
                            }
                        }
                    }
                }
                button(fieldButtonStyle).text("Search").on.click {
                    handleInput(
                        listOf(nameLengthInput),
                        browser.doc.getElementById(outputFieldId),
                        browser.url,
                        browser
                    ) { inputs ->
                        queries.playerNamesByLength(inputs[0])
                    }
                }.new {
                    i(baseballGlyphStyle)
                }
            }
        }
        div(fomantic.ui.hidden.divider)
        div(fomantic.content.id(outputFieldId)).text(output)
    }

    override suspend fun updateUrl(url: KVar<String>, inputs: Map<String, String>) {
        url.value = "/q/$playerNameLength/${inputs[pPlayerNameLength]}"
    }
}
