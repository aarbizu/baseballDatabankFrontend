package org.aarbizu.baseballDatabankFrontend

import kotlinx.js.jso
import mui.material.TableFooter
import mui.material.TablePagination
import mui.material.TableRow
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSelectElement
import react.ChildrenBuilder
import react.StateSetter
import react.dom.events.ChangeEventHandler
import react.dom.events.MouseEvent

class PaginationControls(
    val rowsPerPage: Int,
    val setRowsPerPg: StateSetter<Int>,
    val currentPage: Int,
    val setCurrentPg: StateSetter<Int>
) {

    val handleRppChange: ChangeEventHandler<HTMLElement> = { event ->
        val select = event.target as HTMLSelectElement
        setRowsPerPg(select.value.toInt())
        setCurrentPg(0)
    }

    val handlePgChange: (MouseEvent<HTMLButtonElement, *>?, Number) -> Unit = { _, pageNumber ->
        setCurrentPg(pageNumber as Int)
    }
}

fun ChildrenBuilder.showPageControls(listSize: Int, pagination: PaginationControls) {
    TableFooter {
        TableRow {
            TablePagination {
                rowsPerPageOptions = arrayOf(5, 10, 25, -1)
                colSpan = 5
                count = listSize
                rowsPerPage = pagination.rowsPerPage
                page = pagination.currentPage
                SelectProps = jso { asDynamic()["native"] = true }
                onRowsPerPageChange = pagination.handleRppChange
                onPageChange = pagination.handlePgChange
                showFirstButton = true
                showLastButton = true
            }
        }
    }
}
