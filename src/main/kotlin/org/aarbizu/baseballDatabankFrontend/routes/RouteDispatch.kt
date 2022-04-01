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
private val listSingleSeasonHrLeaders = ListSingleSeasonHRLeaders(crumbs, queryEngine)

/**
 * @see <a href="https://ktor.io/docs/routing-in-ktor.html#wildcard">ktor.io/routing</a>
 */
fun RouteReceiver.dispatch(parameters: Parameters) {
    path("/") {
        defaultRoute.doRoute(this, it)
    }
    path(TOP_LEVEL_MENU_LOCATION) {
        topLevelMenu.doRoute(this, it)
    }

    // TODO i'd like for there to be a better way of handling this, but
    //  the same param qualifiers in kweb aren't in effect as they are in
    //  io.ktor.routing.PathSegmentSelectorBuilder.parseParameter(java.lang.String)

    path("/q/$playerNameLength") {
        nameLengthSearch.doRoute(this, it)
    }
    path("/q/$playerNameLength/{$pPlayerNameLength}") {
        nameLengthSearch.doRoute(this, it)
    }

    path("/q/$playerLastNameSearchQuery/{$pPlayerLastNameParam}") {
        lastNameSearch.doRoute(this, it)
    }
    path("/q/$playerLastNameSearchQuery") {
        lastNameSearch.doRoute(this, it)
    }

    path(
        "/q/$playerNameRegex/{$pPlayerRegexParam}/" +
            "{$pPlayerRegexFnameParam}/" +
            "{$pPlayerRegexLnameParam}/" +
            "{$pPlayerRegexCaseSensitive}"
    ) {
        playerRegexSearch.doRoute(this, it)
    }
    path("/q/$playerNameRegex") {
        playerRegexSearch.doRoute(this, it)
    }

    path("/q/$singleSeasonHrLeaders") {
        listSingleSeasonHrLeaders.doRoute(this, it)
    }
}
