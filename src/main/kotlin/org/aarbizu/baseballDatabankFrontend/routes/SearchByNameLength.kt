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

private fun getCrumb(parameters: Parameters): Crumb {
    return Crumb("Name Length Search", "/q/$playerNameLength/${parameters[playerNameLength]}")
}

fun ElementCreator<*>.handleNameLengthSearch(crumbs: MutableList<Crumb>, parameters: Parameters, queryEngine: QueryEngine) {
    renderNavMenu(getCrumb(parameters), crumbs)
    div(fomantic.ui.hidden.divider)
    div(fomantic.ui.container).new {
        generatePlayerNameLengthForm(queryEngine)
        debugParamsElement(parameters)
    }

    // TODO AA -- now that form() has been removed, handle on.key.press for Enter on the text field
    if (!parameters[pPlayerNameLength].isNullOrEmpty()) {
        val lengthParam = parameters[pPlayerNameLength]!!
        val outputDiv = browser.doc.getElementById("output")
        org.aarbizu.baseballDatabankFrontend.records.PaginatedRecords(
            queryEngine.playerNamesByLength(
                lengthParam
            ), outputDiv
        ).renderTable()
        browser.url.value = "/q/$playerNameLength/?$pPlayerNameLength=$lengthParam"
    }

    div(fomantic.ui.hidden.divider)
    div(fomantic.ui.container.id("errors"))
}

private fun ElementCreator<*>.generatePlayerNameLengthForm(queries: QueryEngine) {
    val output = KVar("")
    var nameLengthInput: InputElement? = null

    div(fomantic.ui.form).new {
        div(fomantic.fields).new {
            div(fomantic.inline.field).new {
                label().text("Name Length")
                nameLengthInput = input(
                    type = InputType.text,
                    name = pPlayerNameLength,
                    initialValue = "",
                    size = 15,
                    placeholder = "14"
                )
            }
            button(fieldButtonStyle).text("Search").on.click {
                GlobalScope.launch {
                    getInputAndRenderResult(
                        listOf(nameLengthInput!!),
                        browser.doc.getElementById("output")
                    ) { inputs ->
                        queries.playerNamesByLength(inputs[0])
                    }
                    browser.url.value = "/q/$playerNameLength/?$pPlayerNameLength=${nameLengthInput!!.getValue().await()}"
                }
            }.new {
                i(baseballGlyphStyle)
            }
        }
    }
    div(fomantic.ui.hidden.divider)
    div(fomantic.content.id("output")).text(output)
}
