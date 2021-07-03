package org.aarbizu.baseballDatabankFrontend.routes

import io.ktor.http.Parameters
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kweb.Element
import kweb.ElementCreator
import kweb.InputElement
import kweb.InputType
import kweb.WebBrowser
import kweb.a
import kweb.div
import kweb.i
import kweb.new
import kweb.plugins.fomanticUI.fomantic
import kweb.state.KVar
import org.aarbizu.baseballDatabankFrontend.records.PaginatedRecords
import org.aarbizu.baseballDatabankFrontend.records.TableRecord

interface RouteHandler {
    fun handleRoute(ec: ElementCreator<*>, params: Map<String, KVar<String>>)
    fun getCrumb(): Crumb
    fun injectCrumbs(): MutableList<Crumb>

    fun doRoute(ec: ElementCreator<*>, parameters: Parameters, params: Map<String, KVar<String>>) {
        renderNavMenu(ec, getCrumb(), injectCrumbs())
        handleRoute(ec, params)
    }

    fun renderNavMenu(ec: ElementCreator<*>, newCrumb: Crumb, crumbs: MutableList<Crumb>) {
        with(ec) {
            div(fomantic.ui.attached.inverted.segment).new {
                div(fomantic.ui.inverted.breadcrumb).new {
                    a(fomantic.section, href = "/").new {
                        div(fomantic.ui.item).new {
                            i(homeIcon)
                        }
                    }
                    appendCrumb(newCrumb, crumbs)
                }
            }
        }
    }

    fun handleInput(
        inputs: List<InputElement?>,
        outputElement: Element,
        url: KVar<String>,
        browser: WebBrowser,
        query: (inputs: List<String>) -> List<TableRecord>
    ) {
        GlobalScope.launch {
            val capturedInputs = getInputAndRenderResult(inputs, outputElement, browser, query)
            updateUrl(url, capturedInputs)
        }
    }

    // is there a bug in ElementReader?? name doesn't get used in the library call
    suspend fun getAttrib(browser: WebBrowser, elementId: String, name: String): String {
        return browser.callJsFunctionWithResult(
            "return document.getElementById({}).getAttribute({})", JsonPrimitive(elementId), JsonPrimitive(name)).jsonPrimitive.content
    }

    suspend fun updateUrl(url: KVar<String>, inputs: Map<String, String>)

    suspend fun getInputAndRenderResult(
        inputs: List<InputElement?>,
        outputElement: Element,
        browser: WebBrowser,
        query: (inputs: List<String>) -> List<TableRecord>
    ): Map<String, String> {
        val params = mutableMapOf<String, String>()
        inputs.forEach {
            it?.let {
                when (getAttrib(browser, it.id, "type")) {
                    InputType.text.name -> {
                        val textValue = it.getValue()
                        val pName = getAttrib(browser, it.id, "name")
                        params[pName] = textValue
                    }
                    InputType.checkbox.name -> {
                        val checkValue = getAttrib(browser, it.id, "checked")
                        val pName = getAttrib(browser, it.id, "name")
                        params[pName] = checkValue
                    }
                    else -> params[getAttrib(browser, it.id, "name")] =
                        "unknown input, id: ${it.id}, ${it.element.id}"
                }
            }
        }
        getQueryResult(params.values.toList(), outputElement, query)
        return params
    }

    fun getQueryResult(
        params: List<String>,
        outputElement: Element,
        query: (inputs: List<String>) -> List<TableRecord>
    ) {
        PaginatedRecords(query(params), outputElement).renderTable()
    }
}
