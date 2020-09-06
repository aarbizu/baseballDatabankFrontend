package org.aarbizu.baseballDatabankFrontend.records

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

class PaginatedRecords(
    private val records: List<TableRecord>,
    private val element: Element,
    private val pageSize: Int = 10
) {
    private val size = records.size
    private var from: Int = 0
    private var to: Int = if (size < pageSize) size else from + pageSize

    private fun nextPage(): Boolean {
        var changed = true
        if (to < size) {
            from += pageSize
            to = if (to + pageSize > size) size else to + pageSize
        } else {
            changed = false
        }
        return changed
    }

    private fun prevPage(): Boolean {
        var changed = true
        if (from > 0) {
            from = if (from - pageSize < 0) 0 else from - pageSize
            to = if (to == size) from + pageSize else to - pageSize
        } else {
            changed = false
        }
        return changed
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
                    div(mapOf("class" to "link item", "id" to "previous-item")).text("Prev").on.click {
                        if (prevPage()) {
                            renderTable()
                            browser.doc.getElementById("next-item").removeClasses("disabled")
                            if (from == 0) browser.doc.getElementById("previous-item").addClasses("disabled")
                        }
                    }
                    div(mapOf("class" to "link item", "id" to "next-item")).text("Next").on.click {
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
