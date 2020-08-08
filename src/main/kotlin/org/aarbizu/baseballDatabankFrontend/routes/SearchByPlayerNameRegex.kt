package org.aarbizu.baseballDatabankFrontend.routes

import io.ktor.http.Parameters
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kweb.ElementCreator
import kweb.InputElement
import kweb.InputType
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

private fun getCrumb(parameters: Parameters): Crumb {
    return Crumb("Regex Search", "/q/$playerNameRegex/${parameters[playerNameRegex]}")
}

fun ElementCreator<*>.handlePlayerNameRegexSearch(crumbs: MutableList<Crumb>, parameters: Parameters, queryEngine: QueryEngine) {
    renderNavMenu(getCrumb(parameters), crumbs)
    div(fomantic.ui.hidden.divider)
    div(fomantic.ui.container).new {
        generatePlayerNameRegexSearchForm(queryEngine)
        debugParamsElement(parameters)
    }
    if (parameters[pPlayerRegexParam]?.isNotEmpty()!!) {
        val regex = parameters[pPlayerRegexParam]!!
        val useFirst = parameters[pPlayerRegexFnameParam]!!
        val useLast = parameters[pPlayerRegexLnameParam]!!
        val useCase = parameters[pPlayerRegexCaseSensitive]!!
        val outputEl = browser.doc.getElementById("names")
        PaginatedRecords(
            queryEngine.playerNameRegexSearch(
                regex,
                useFirst.toBoolean(),
                useLast.toBoolean(),
                useCase.toBoolean()
            ), outputEl
        ).renderTable()
    }
    div(fomantic.ui.hidden.divider)
    div(fomantic.ui.container.id("errors"))
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
                    placeholder = """ Regex, e.g. '.(?:na){2}.*' """.trim()
                )
                nameRegexInput?.value = nameRegex
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
                GlobalScope.launch {
                    getInputAndRenderResult(
                        listOf(nameRegexInput!!, firstNameMatch!!, lastNameMatch!!, caseSensitive!!),
                        browser.doc.getElementById("names")
                    ) { inputs ->
                        queries.playerNameRegexSearch(
                            inputs[0],
                            inputs[1].isNotEmpty(),
                            inputs[2].isNotEmpty(),
                            inputs[3].isNotEmpty()
                        )
                    }

                    browser.url.value = "/q/$playerNameRegex/?$pPlayerRegexParam=${nameRegexInput!!.getValue().await()}&" +
                            "$pPlayerRegexFnameParam=${firstNameMatch!!.getValue().await()}&" +
                            "$pPlayerRegexLnameParam=${lastNameMatch!!.getValue().await()}&" +
                            "$pPlayerRegexCaseSensitive=${caseSensitive!!.getValue().await()}"
                }
            }
        }
    }

    div(fomantic.ui.hidden.divider)
    div(fomantic.content.id("names")).text(names)
}
