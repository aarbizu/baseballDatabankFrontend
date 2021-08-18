package org.aarbizu.baseballDatabankFrontend.routes

import kweb.Element
import kweb.ElementCreator
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

    fun doRoute(ec: ElementCreator<*>, params: Map<String, KVar<String>>) {
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
        kvars: Array<KVar<*>>,
        outputElementId: String,
        browser: WebBrowser,
        query: (inputs: List<String>) -> List<TableRecord>
    ) {
        getQueryResult(kvars.map { it.value.toString() }.toList(), getElementById(browser, outputElementId), query)
        updateUrl(browser.url, kvars)
    }

    fun getElementById(
        browser: WebBrowser,
        id: String
    ): Element {
        return browser.doc.getElementById(id)
    }

    fun updateUrl(url: KVar<String>, inputs: Array<KVar<*>>)

    fun getQueryResult(
        params: List<String>,
        outputElement: Element,
        query: (inputs: List<String>) -> List<TableRecord>
    ) {
        PaginatedRecords(query(params), outputElement).renderTable()
    }
}
