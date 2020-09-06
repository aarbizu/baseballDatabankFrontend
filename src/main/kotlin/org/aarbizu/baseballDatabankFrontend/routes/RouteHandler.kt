package org.aarbizu.baseballDatabankFrontend.routes

import io.ktor.http.Parameters
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kweb.Element
import kweb.ElementCreator
import kweb.InputElement
import kweb.InputType
import kweb.a
import kweb.div
import kweb.i
import kweb.new
import kweb.plugins.fomanticUI.fomantic
import kweb.state.KVar
import org.aarbizu.baseballDatabankFrontend.records.PaginatedRecords
import org.aarbizu.baseballDatabankFrontend.records.TableRecord

interface RouteHandler {
    fun handleRoute(ec: ElementCreator<*>, parameters: Parameters)
    fun getCrumb(parameters: Parameters): Crumb
    fun injectCrumbs(): MutableList<Crumb>

    fun doRoute(ec: ElementCreator<*>, parameters: Parameters) {
        renderNavMenu(ec, getCrumb(parameters), injectCrumbs())
        handleRoute(ec, parameters)
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
        inputs: List<InputElement>,
        outputElement: Element,
        url: KVar<String>,
        query: (inputs: List<String>) -> List<TableRecord>
    ) {
        GlobalScope.launch {
            val capturedInputs = getInputAndRenderResult(inputs, outputElement, query)
            updateUrl(url, capturedInputs)
        }
    }

    suspend fun updateUrl(url: KVar<String>, inputs: Map<String, String>)

    suspend fun getInputAndRenderResult(
        inputs: List<InputElement>,
        outputElement: Element,
        query: (inputs: List<String>) -> List<TableRecord>
    ): Map<String, String> {
        val params = mutableMapOf<String, String>()
        inputs.forEach {
            when (it.read.attribute("type").await()) {
                InputType.text.name -> {
                    val textValue = it.getValue().await()
                    val pName = it.read.attribute("name").await().toString()
                    params[pName] = textValue
                }
                InputType.checkbox.name -> {
                    val checkValue = it.read.attribute("checked").await().toString()
                    val pName = it.read.attribute("name").await().toString()
                    params[pName] = checkValue
                }
                else -> params[it.read.attribute("name").await().toString()] =
                    "unknown input, id: ${it.id!!}, ${it.element.jsExpression}"
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
