package org.aarbizu.baseballDatabankFrontend.records

import kotlinx.serialization.json.JsonPrimitive
import kweb.Element
import kweb.div
import kweb.new
import kweb.plugins.fomanticUI.fomantic
import kweb.table
import kweb.tbody
import kweb.td
import kweb.th
import kweb.thead
import kweb.tr
import kotlin.math.max
import kotlin.math.min

class PaginatedRecords(
    private val records: List<TableRecord>,
    private val element: Element,
    private val pageSize: Int = 10
) {
    private val size = records.size
    private var from = 0
    private var to = min(from + pageSize, size)

    private fun nextPage() = if (to < size) {
        from += pageSize
        to = min(to + pageSize, size)
        true
    } else {
        false
    }

    private fun prevPage() = if (from > 0) {
        from = max(from - pageSize, 0)
        to = if (to == size) from + pageSize else to - pageSize
        true
    } else {
        false
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
                                row.render(this)
                            }
                        }
                    }
                }
                div(fomantic.ui.compact.menu).new {
                    div(mapOf("class" to JsonPrimitive("link item"), "id" to JsonPrimitive("previous-item"))).text("Prev").on.click {
                        if (prevPage()) {
                            renderTable()
                            browser.doc.getElementById("next-item").removeClasses("disabled")
                            if (from == 0) browser.doc.getElementById("previous-item").addClasses("disabled")
                        }
                    }
                    div(mapOf("class" to JsonPrimitive("link item"), "id" to JsonPrimitive("next-item"))).text("Next").on.click {
                        if (nextPage()) {
                            renderTable()
                            browser.doc.getElementById("previous-item").removeClasses("disabled")
                            if (to == size) browser.doc.getElementById("next-item").addClasses("disabled")
                        }
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
