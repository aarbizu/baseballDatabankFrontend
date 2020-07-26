package org.aarbizu.baseballDatabankFrontend.config

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
import org.aarbizu.baseballDatabankFrontend.TableRecord

class PaginatedRecords(
    private val records: List<TableRecord>,
    private val element: Element,
    private val pageSize: Int = 10
) {
    private val size = records.size
    private var from: Int = 0
    private var to: Int = if (size < pageSize) size else from + pageSize

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
