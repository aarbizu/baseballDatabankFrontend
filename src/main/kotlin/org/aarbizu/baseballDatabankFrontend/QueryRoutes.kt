package org.aarbizu.baseballDatabankFrontend

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kweb.Element
import kweb.ElementCreator
import kweb.InputElement
import kweb.InputType
import kweb.a
import kweb.button
import kweb.div
import kweb.form
import kweb.h1
import kweb.i
import kweb.id
import kweb.input
import kweb.label
import kweb.new
import kweb.plugins.fomanticUI.fomantic
import kweb.routing.RouteReceiver
import kweb.state.KVar
import kweb.table
import kweb.tbody
import kweb.td
import kweb.th
import kweb.thead
import kweb.tr
import org.apache.commons.lang3.time.StopWatch
import org.slf4j.LoggerFactory

private val massiveButtonStyle = mapOf("class" to "ui massive orange right labeled icon button")
private val buttonStyle = mapOf("class" to "ui large orange right labeled icon button")
private val baseballGlyphStyle = mapOf("class" to "baseball ball icon")
private val subHeaderStyle = mapOf("class" to "sub header")
private val homeIcon = mapOf("class" to "home icon")
private val fieldButtonStyle = mapOf("class" to buttonStyle["class"] + " field")

private const val playerNameLengthQuery = "player-name-by-len"
private const val playerNameSearchQuery = "player-name"

private val logger = LoggerFactory.getLogger("QueryRoutes")

fun RouteReceiver.getRoutePaths() {
    path("/") {
        renderNavMenu()
        div(fomantic.ui.center.aligned.container).new {
            div(fomantic.ui.hidden.divider)
            div(fomantic.content).new {
                h1(fomantic.ui.icon.header).new {
                    i(baseballGlyphStyle)
                    div(fomantic.content).text("Baseball Databank").new {
                        div(subHeaderStyle).text("Historical baseball database derived from: ").new {
                            a(href = "https://github.com/chadwickbureau/baseballdatabank")
                                .text("The Chadwick Baseball Databank").tag
                        }
                    }
                }
                div(fomantic.content).new {
                    a(href = "http://chadwick-bureau.com/")
                        .text("Visit the Chadwick Baseball Bureau")
                }
                div(fomantic.ui.divider)
                button(massiveButtonStyle).text("Get Started").on.click {
                    browser.url.value = "/q/begin"
                }.new {
                    i(fomantic.ui.icon.right.arrow)
                }
            }
        }
    }
    path("/q/{type}") { _ ->
        renderNavMenu()
        div(fomantic.ui.hidden.divider)
        div(fomantic.ui.container).new {
            div(fomantic.ui.two.item.menu).new {
                a(fomantic.ui.item).text("Player Name By Length").on.click {
                    val queryElement = browser.doc.getElementById("query")
                    handleQuery(playerNameLengthQuery, queryElement)
                    browser.url.value = "/q/$playerNameLengthQuery"
                }
                a(fomantic.ui.item).text("Player Name Search").on.click {
                    val queryElement = browser.doc.getElementById("query")
                    handleQuery(playerNameSearchQuery, queryElement)
                    browser.url.value = "/q/$playerNameSearchQuery"
                }
            }

            div(fomantic.ui.container.id("query"))
        }
    }
}

private fun ElementCreator<*>.renderNavMenu() {
    div(fomantic.ui.top.attached.menu.inverted).new {
        a(href = "/").new {
            div(fomantic.ui.item).new {
                i(homeIcon)
            }
        }
    }
}

private fun handleQuery(queryType: String, element: Element) {
    val queryEngine = QueryEngine()

    if (queryType == playerNameLengthQuery) {
        generatePlayerNameLengthForm(element, queryEngine)
    }

    if (queryType == playerNameSearchQuery) {
        element.removeChildren().new {
            div(fomantic.content).text("insert player name search query")
        }
    }
}

private fun generatePlayerNameLengthForm(element: Element, queries: QueryEngine) {
    val output = KVar("")
    var length: String
    var nameLengthInput: InputElement? = null
    element.removeChildren().new {
        form(fomantic.ui.form).new {
            div(fomantic.fields).new {
                div(fomantic.three.wide.field).new {
                    label("Name Length")
                    nameLengthInput = input(
                        type = InputType.text,
                        name = "name-length",
                        initialValue = "",
                        size = 2,
                        placeholder = "Name Length"
                    )
                }
                button(fieldButtonStyle).text("Search").on.click {
                    GlobalScope.launch {
                        length = nameLengthInput!!.getValue().await()
                        val timer = StopWatch.createStarted()
                        val players = queries.playerNamesByLength(length)
                        logger.info("queryPlayers: $timer")
                        timer.reset()
                        PaginatedRecords(players, browser.doc.getElementById("output")).renderTable()
                        logger.info("renderTable: $timer")
                    }
                }.new {
                    i(baseballGlyphStyle)
                }
            }
        }
        div(fomantic.ui.hidden.divider)
        div(fomantic.content.id("output")).text(output)
    }
}

class PaginatedRecords(
    private val records: List<TableRecord>,
    private val element: Element,
    private val pageSize: Int = 10
) {
    private val size = records.size
    private var from: Int = 0
    private var to: Int = from + pageSize

    private fun nextPage() {
        if (to < size) {
            from += pageSize
            to = if (to + pageSize > size) size else to + pageSize
        }
    }

    private fun prevPage() {
        if (from > 0) {
            from = if (from - pageSize < 0) 0 else from - pageSize
            to = if (to == size) from + pageSize else to - pageSize
        }
    }

    fun renderTable() {
        if (size > 0) {
            element.removeChildren().new {
                div(fomantic.content).text("$size result found.")
                table(fomantic.ui.celled.table).new {
                    thead().new {
                        tr().new {
                            th().text("#")
                            records[0].headers().forEach {
                                th().text(it)
                            }
                        }
                    }
                    tbody().new {
                        var recNum = from + 1
                        records.subList(from, to).forEach { row ->
                            tr().new {
                                td().text("${recNum++}")
                                row.cells().forEach { cell ->
                                    td().text(cell)
                                }
                            }
                        }
                    }
                }
                div(fomantic.ui.compact.menu).new {
                    div(mapOf("class" to "link item")).text("Prev").on.click {
                        prevPage()
                        renderTable()
                    }
                    div(mapOf("class" to "link item")).text("Next").on.click {
                        nextPage()
                        renderTable()
                    }
                }
            }
        } else {
            element.removeChildren().new {
                div(fomantic.content).text("No results.")
            }
        }
    }
}
