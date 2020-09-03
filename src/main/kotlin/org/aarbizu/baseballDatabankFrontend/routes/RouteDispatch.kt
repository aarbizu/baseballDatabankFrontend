package org.aarbizu.baseballDatabankFrontend.routes

import io.ktor.http.Parameters
import kweb.routing.RouteReceiver
import org.aarbizu.baseballDatabankFrontend.db.DB
import org.aarbizu.baseballDatabankFrontend.db.QueryEngine

var debug = false

private val queryEngine = QueryEngine(DB)
private val crumbs = mutableListOf<Crumb>()

private val defaultRoute = DefaultRoute(crumbs)
private val topLevelMenu = TopLevelMenu(crumbs)
private val nameLengthSearch = SearchByNameLength(crumbs, queryEngine)
private val lastNameSearch = SearchByPlayerLastName(crumbs, queryEngine)
private val playerRegexSearch = SearchByPlayerNameRegex(crumbs, queryEngine)

fun RouteReceiver.dispatch(parameters: Parameters) {
    path("/") {
        defaultRoute.doRoute(this, parameters)
    }
    path(TOP_LEVEL_MENU_LOCATION) {
        topLevelMenu.doRoute(this, parameters)
    }
    path("/q/$playerNameLength/{lengthParam}") {
        nameLengthSearch.doRoute(this, parameters)
    }
    path("/q/$playerLastNameSearchQuery/{nameParam}") {
        lastNameSearch.doRoute(this, parameters)
    }
    path("/q/$playerNameRegex/{regexParam...}") {
        playerRegexSearch.doRoute(this, parameters)
    }
}
