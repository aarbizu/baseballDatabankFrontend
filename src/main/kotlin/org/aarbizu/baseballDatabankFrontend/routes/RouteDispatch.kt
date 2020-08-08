package org.aarbizu.baseballDatabankFrontend.routes

import io.ktor.http.Parameters
import kotlinx.coroutines.future.await
import kweb.Element
import kweb.InputElement
import kweb.InputType
import kweb.routing.RouteReceiver
import org.aarbizu.baseballDatabankFrontend.db.QueryEngine
import org.aarbizu.baseballDatabankFrontend.records.PaginatedRecords
import org.aarbizu.baseballDatabankFrontend.records.TableRecord

var debug = false

private val queryEngine = QueryEngine()
private val crumbs = mutableListOf<Crumb>()

fun RouteReceiver.dispatch(parameters: Parameters) {
    path("/") {
        handleDefaultRoute(crumbs)
    }
    path(TOP_LEVEL_MENU_LOCATION) {
        handleTopLevelMenu(crumbs)
    }
    path("/q/$playerNameLength/{lengthParam}") {
        handleNameLengthSearch(crumbs, parameters, queryEngine)
    }
    path("/q/$playerLastNameSearchQuery/{nameParam}") {
        handleLastNameSearch(crumbs, parameters, queryEngine)
    }
    path("/q/$playerNameRegex/{regexParam...}") {
        handlePlayerNameRegexSearch(crumbs, parameters, queryEngine)
    }
}

suspend fun getInputAndRenderResult(
    inputs: List<InputElement>,
    outputElement: Element,
    query: (inputs: List<String>) -> List<TableRecord>
) {
    val params = mutableListOf<String>()
    inputs.forEach {
        when (it.read.attribute("type").await()) {
            InputType.text.name -> {
                params.add(it.getValue().await())
            }
            InputType.checkbox.name -> {
                params.add(it.read.attribute("checked").await().toString())
            }
            else -> params.add("unknown, id: ${it.id!!}")
        }
    }
    getQueryResult(params, outputElement, query)
}

fun getQueryResult(
    params: List<String>,
    outputElement: Element,
    query: (inputs: List<String>) -> List<TableRecord>
) {
    PaginatedRecords(query(params), outputElement).renderTable()
}
