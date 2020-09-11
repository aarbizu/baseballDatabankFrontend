package org.aarbizu.baseballDatabankFrontend.routes

import io.ktor.http.Parameters
import kweb.ElementCreator
import kweb.InputElement
import kweb.InputType
import kweb.WebBrowser
import kweb.button
import kweb.div
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

    override fun handleRoute(ec: ElementCreator<*>, parameters: Parameters) {
        with(ec) {
            div(fomantic.ui.hidden.divider)
            div(fomantic.ui.container).new {
                generatePlayerNameRegexSearchForm(queryEngine)
                debugParamsElement(parameters)
            }

            handleQueryStringIfPresent(parameters, browser)

            div(fomantic.ui.hidden.divider)
            div(fomantic.ui.container.id("errors"))
        }
    }

    private fun handleQueryStringIfPresent(parameters: Parameters, browser: WebBrowser) {
        if (parameters[pPlayerRegexParam]?.isNotEmpty()!!) {
            val regex = parameters[pPlayerRegexParam]!!
            val useFirst = parameters[pPlayerRegexFnameParam]!!
            val useLast = parameters[pPlayerRegexLnameParam]!!
            val useCase = parameters[pPlayerRegexCaseSensitive]!!
            val outputEl = browser.doc.getElementById(outputElementId)

            PaginatedRecords(
                queryEngine.playerNameRegexSearch(
                    regex,
                    useFirst.toBoolean(),
                    useLast.toBoolean(),
                    useCase.toBoolean()
                ), outputEl
            ).renderTable()
        }
    }

    private fun ElementCreator<*>.generatePlayerNameRegexSearchForm(queries: QueryEngine) {
        val names = KVar("")
        var nameRegexInput: InputElement? = null
        var firstNameMatch: InputElement? = null
        var lastNameMatch: InputElement? = null
        var caseSensitive: InputElement? = null
        val nameRegex = KVar("")
        var useFirstName = true
        var useLastName = true
        var isCaseSensitive = true

        div(fomantic.ui.form).new {
            div(fomantic.fields).new {
                div(fomantic.inline.field).new {
                    label().text("Name Regex")
                    nameRegexInput = input(
                        type = InputType.text,
                        name = pPlayerRegexParam,
                        initialValue = "",
                        size = 32,
                        placeholder = """ Regex, e.g. '.(?:na){2}.*' """.trim(),
                        attributes = mutableMapOf("id" to regexFieldId)
                    )
                    nameRegexInput?.value = nameRegex
                    nameRegexInput!!.on.keypress { ke ->
                        if (ke.code == "Enter") {
                            handleInput(
                                listOf(nameRegexInput!!, firstNameMatch!!, lastNameMatch!!, caseSensitive!!),
                                browser.doc.getElementById(outputElementId),
                                browser.url
                            ) { inputs -> queries.playerNameRegexSearch(
                                    inputs[0],
                                    inputs[1].isNotEmpty(),
                                    inputs[2].isNotEmpty(),
                                    inputs[3].isNotEmpty()
                                )
                            }
                        }
                    }
                }
                div(fomantic.inline.field).new {
                    label().text("First Name")
                    firstNameMatch = input(
                        type = InputType.checkbox,
                        name = pPlayerRegexFnameParam
                    )
                    firstNameMatch!!.checked(useFirstName)
                    firstNameMatch!!.on.click {
                        useFirstName = !useFirstName
                        firstNameMatch!!.checked(useFirstName)
                    }
                }
                div(fomantic.inline.field).new {
                    label().text("Last Name")
                    lastNameMatch = input(
                        type = InputType.checkbox,
                        name = pPlayerRegexLnameParam
                    )
                    lastNameMatch!!.checked(useLastName)
                    lastNameMatch!!.on.click {
                        useLastName = !useLastName
                        lastNameMatch!!.checked(useLastName)
                    }
                }
                div(fomantic.inline.field).new {
                    label().text("Case sensitive")
                    caseSensitive = input(
                        type = InputType.checkbox,
                        name = pPlayerRegexCaseSensitive
                    )
                    caseSensitive!!.checked(isCaseSensitive)
                    caseSensitive!!.on.click {
                        isCaseSensitive = !isCaseSensitive
                        caseSensitive!!.checked(isCaseSensitive)
                    }
                }
                button(fieldButtonStyle).text("Search").on.click {
                    handleInput(
                        listOf(nameRegexInput!!, firstNameMatch!!, lastNameMatch!!, caseSensitive!!),
                        browser.doc.getElementById(outputElementId),
                        browser.url
                    ) { inputs -> queries.playerNameRegexSearch(
                            inputs[0],
                            inputs[1].isNotEmpty(),
                            inputs[2].isNotEmpty(),
                            inputs[3].isNotEmpty()
                        )
                    }
                }
            }
        }

        div(fomantic.ui.hidden.divider)
        div(fomantic.content.id(outputElementId)).text(names)
    }

    override fun getCrumb(parameters: Parameters) =
        Crumb("Regex Search", "/q/$playerNameRegex/${parameters[playerNameRegex]}")

    override fun injectCrumbs() = crumbs

    override suspend fun updateUrl(url: KVar<String>, inputs: Map<String, String>) {
        url.value = "/q/$playerNameRegex/?$pPlayerRegexParam=${inputs[pPlayerRegexParam]}&" +
                    "$pPlayerRegexFnameParam=${inputs[pPlayerRegexFnameParam]}&" +
                    "$pPlayerRegexLnameParam=${inputs[pPlayerRegexLnameParam]}&" +
                    "$pPlayerRegexCaseSensitive=${inputs[pPlayerRegexCaseSensitive]}"
    }
}
