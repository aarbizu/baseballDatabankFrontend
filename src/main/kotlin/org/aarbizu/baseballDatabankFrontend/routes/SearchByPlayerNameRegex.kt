package org.aarbizu.baseballDatabankFrontend.routes

import kotlinx.serialization.json.JsonPrimitive
import kweb.ElementCreator
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

const val playerNameRegex = "player-name-regex"
const val pPlayerRegexParam = "regex"
const val pPlayerRegexFnameParam = "regex-first-name"
const val pPlayerRegexLnameParam = "regex-last-name"
const val pPlayerRegexCaseSensitive = "regex-case-sensitive"
const val regexFieldId = "regex-field"

class SearchByPlayerNameRegex(private val crumbs: MutableList<Crumb>, private val queryEngine: QueryEngine) : RouteHandler {
    private val outputElementId = "names"

    override fun handleRoute(ec: ElementCreator<*>, params: Map<String, KVar<String>>) {
        with(ec) {
            div(fomantic.ui.hidden.divider)
            div(fomantic.ui.container).new {
                generatePlayerNameRegexSearchForm(queryEngine)
                debugParamsElement(params)
            }

            handleQueryStringIfPresent(params, browser)

            div(fomantic.ui.hidden.divider)
            div(fomantic.ui.container.id("errors"))
        }
    }

    private fun handleQueryStringIfPresent(parameters: Map<String, KVar<String>>, browser: WebBrowser) {
        if (parameters[pPlayerRegexParam]?.value?.isNotEmpty()!!) {
            val regexEnc = parameters[pPlayerRegexParam]?.value
            val regexDec = java.net.URLDecoder.decode(regexEnc, "utf-8")
            val useFirst = parameters[pPlayerRegexFnameParam]?.value.toString()
            val useLast = parameters[pPlayerRegexLnameParam]?.value.toString()
            val useCase = parameters[pPlayerRegexCaseSensitive]?.value.toString()
            val outputEl = browser.doc.getElementById(outputElementId)

            PaginatedRecords(
                queryEngine.playerNameRegexSearch(
                    regexDec,
                    useFirst.toBoolean(),
                    useLast.toBoolean(),
                    useCase.toBoolean()
                ),
                outputEl
            ).renderTable()
        }
    }

    private fun ElementCreator<*>.generatePlayerNameRegexSearchForm(queries: QueryEngine) {
        val names = KVar("")
        lateinit var firstNameMatch: KVar<Boolean>
        lateinit var lastNameMatch: KVar<Boolean>
        lateinit var caseSensitive: KVar<Boolean>
        val nameRegex = KVar("")

        div(fomantic.ui.form).new {
            div(fomantic.fields).new {
                div(fomantic.inline.field).new {
                    label().text("Name Regex")
                    val nameRegexElement = input(
                        type = InputType.text,
                        name = pPlayerRegexParam,
                        initialValue = "",
                        size = 32,
                        placeholder = """ .(?:na){2}.* """.trim(),
                        attributes = mutableMapOf("id" to JsonPrimitive(regexFieldId))
                    )
                    nameRegexElement.value = nameRegex
                    nameRegexElement.on.keypress { ke ->
                        if (ke.code == "Enter") {
                            handleInput(
                                arrayOf(nameRegex, firstNameMatch, lastNameMatch, caseSensitive),
                                outputElementId,
                                browser
                            ) { inputs ->
                                queries.playerNameRegexSearch(
                                    inputs[0],
                                    inputs[1].toBoolean(),
                                    inputs[2].toBoolean(),
                                    inputs[3].toBoolean()
                                )
                            }
                        }
                    }
                }
                div(fomantic.inline.field).new {
                    label().text("First Name")
                    val firstNameMatchEl = input(
                        type = InputType.checkbox,
                        name = pPlayerRegexFnameParam,
                        initialValue = "true"
                    )
                    firstNameMatch = firstNameMatchEl.checked(true)
                }
                div(fomantic.inline.field).new {
                    label().text("Last Name")
                    val lastNameMatchEl = input(
                        type = InputType.checkbox,
                        name = pPlayerRegexLnameParam,
                        initialValue = "true"
                    )
                    lastNameMatch = lastNameMatchEl.checked(true)
                }
                div(fomantic.inline.field).new {
                    label().text("Case sensitive")
                    val caseSensitiveEl = input(
                        type = InputType.checkbox,
                        name = pPlayerRegexCaseSensitive,
                        initialValue = "true"
                    )
                    caseSensitive = caseSensitiveEl.checked(true)
                }
                button(fieldButtonStyle).text("Search").on.click {
                    handleInput(
                        arrayOf(nameRegex, firstNameMatch, lastNameMatch, caseSensitive),
                        outputElementId,
                        browser
                    ) { inputs ->
                        queries.playerNameRegexSearch(
                            inputs[0],
                            inputs[1].toBoolean(),
                            inputs[2].toBoolean(),
                            inputs[3].toBoolean()
                        )
                    }
                }.new {
                    i(baseballGlyphStyle)
                }
            }
        }

        div(fomantic.ui.hidden.divider)
        div(fomantic.content.id(outputElementId)).text(names)
    }

    override fun getCrumb() =
        Crumb("Regex Search", "/q/$playerNameRegex")

    override fun injectCrumbs() = crumbs

    override fun updateUrl(url: KVar<String>, inputs: Array<KVar<*>>) {
        val encRegex = java.net.URLEncoder.encode(inputs[0].value.toString(), "utf-8")
        url.value =
            "/q/$playerNameRegex/$encRegex/" +
            "${inputs[1].value}/" +
            "${inputs[2].value}/" +
            "${inputs[3].value}"
    }
}
