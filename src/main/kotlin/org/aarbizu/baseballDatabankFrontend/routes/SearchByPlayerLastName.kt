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
import kweb.i
import kweb.id
import kweb.input
import kweb.label
import kweb.new
import kweb.plugins.fomanticUI.fomantic
import kweb.state.KVar
import org.aarbizu.baseballDatabankFrontend.db.QueryEngine
import org.aarbizu.baseballDatabankFrontend.records.PaginatedRecords

private fun getCrumb(parameters: Parameters): Crumb {
    return Crumb("Last Name", "/q/$playerLastNameSearchQuery/${parameters[playerLastNameSearchQuery]}")
}

fun ElementCreator<*>.handleLastNameSearch(crumbs: MutableList<Crumb>, parameters: Parameters, queryEngine: QueryEngine) {
    renderNavMenu(getCrumb(parameters), crumbs)
    div(fomantic.ui.hidden.divider)
    div(fomantic.ui.container).new {
        generatePlayerNameSearchForm(queryEngine)
        debugParamsElement(parameters)
    }

    // TODO AA -- these blocks of handling GETs should, I think, be handled better by on.keypress handlers
    //  in the input elements
    if (parameters[pPlayerLastNameParam]?.isNotEmpty()!!) {
        val lastNameParam = parameters[pPlayerLastNameParam]!!
        val outputDiv = browser.doc.getElementById("names")
        PaginatedRecords(
            queryEngine.playerNameSearch("%${lastNameParam.toLowerCase()}%"),
            outputDiv
        ).renderTable()
        browser.url.value = "/q/$playerLastNameSearchQuery/?$pPlayerLastNameParam=$lastNameParam"
    }

    div(fomantic.ui.hidden.divider)
    div(fomantic.ui.container.id("errors"))
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
            }
            button(fieldButtonStyle).text("Search").on.click {
                GlobalScope.launch {
                    getInputAndRenderResult(
                        listOf(nameFragmentInput!!),
                        browser.doc.getElementById("names")
                    ) { inputs ->
                        queries.playerNameSearch("%${inputs[0].toLowerCase()}%")
                    }
                    browser.url.value = "/q/$playerLastNameSearchQuery/?$pPlayerLastNameParam=${nameFragmentInput!!.getValue().await()}"
                }
            }.new {
                i(baseballGlyphStyle)
            }
        }
    }
    div(fomantic.ui.hidden.divider)
    div(fomantic.content.id("names")).text(output)
}
