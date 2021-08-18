package org.aarbizu.baseballDatabankFrontend.routes

import kweb.ElementCreator
import kweb.div
import kweb.id
import kweb.new
import kweb.plugins.fomanticUI.fomantic
import kweb.state.KVar
import org.aarbizu.baseballDatabankFrontend.db.QueryEngine

const val singleSeasonHrLeaders = "single-season-hr-leaders"

class ListSingleSeasonHRLeaders(
    private val crumbs: MutableList<Crumb>,
    private val queryEngine: QueryEngine
) : RouteHandler {
    private val outputElementId = "singleSeasonHrTotals"

    override fun handleRoute(ec: ElementCreator<*>, params: Map<String, KVar<String>>) {
        with(ec) {
            div(fomantic.ui.hidden.divider)
            div(fomantic.ui.container).new {
                getListOfHRTotals(queryEngine)
            }
        }
    }

    private fun ElementCreator<*>.getListOfHRTotals(queryEngine: QueryEngine) {
        val output = KVar("")
        div(fomantic.content.id(outputElementId)).text(output)

        // todo add a radio button to toggle between first name onnly and first+middle
        handleInput(arrayOf(), outputElementId, browser) { queryEngine.singleSeasonHrTotals(true) }
    }

    override fun getCrumb(): Crumb = Crumb("single season HR totals", "/q/$singleSeasonHrLeaders")

    override fun injectCrumbs() = crumbs

    override fun updateUrl(url: KVar<String>, inputs: Array<KVar<*>>) {
        // no-op?
    }
}
