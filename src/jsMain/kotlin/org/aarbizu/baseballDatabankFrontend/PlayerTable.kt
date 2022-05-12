package org.aarbizu.baseballDatabankFrontend

import csstype.TextAlign
import csstype.pct
import csstype.px
import kotlinx.js.jso
import mui.icons.material.SportsBaseballTwoTone
import mui.material.Box
import mui.material.Container
import mui.material.IconButton
import mui.material.Link
import mui.material.LinkUnderline
import mui.material.Paper
import mui.material.Stack
import mui.material.Table
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableContainer
import mui.material.TableFooter
import mui.material.TablePagination
import mui.material.TableRow
import mui.material.Tooltip
import mui.system.sx
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSelectElement
import react.ChildrenBuilder
import react.FC
import react.Props
import react.ReactElement
import react.create
import react.dom.aria.ariaLabel
import react.dom.events.ChangeEventHandler
import react.dom.events.MouseEvent
import react.dom.html.AnchorTarget
import react.dom.html.ReactHTML.div
import react.key
import react.useState
import kotlin.math.max

external interface PlayerTableProps : Props {
    var playerList: List<SimplePlayerRecord>
    var listType: String
}

val PlayerTable =
    FC<PlayerTableProps> { props ->
        val (rowsPerPg, setRowsPerPg) = useState(10)
        val (currentPg, setCurrentPg) = useState(0)

        val handleRppChange: ChangeEventHandler<HTMLElement> = { event ->
            val select = event.target as HTMLSelectElement
            setRowsPerPg(select.value.toInt())
            setCurrentPg(0)
        }
        val handlePgChange: (MouseEvent<HTMLButtonElement, *>?, Number) -> Unit = { _, pageNumber ->
            setCurrentPg(pageNumber as Int)
        }
        Container {
            sx { width = 100.pct }

            TableContainer {
                component = Paper.create().type
                Table {
                    ariaLabel = "players"

                    TableBody {
                        showPlayers(props.playerList, props.listType, rowsPerPg, currentPg)
                    }

                    if (props.playerList.size > 10) {
                        TableFooter {
                            TableRow {
                                TablePagination {
                                    rowsPerPageOptions = arrayOf(5, 10, 25, -1)
                                    colSpan = 5
                                    count = props.playerList.size
                                    rowsPerPage = rowsPerPg
                                    page = currentPg
                                    SelectProps = jso { asDynamic()["native"] = true }
                                    onRowsPerPageChange = handleRppChange
                                    onPageChange = handlePgChange
                                    showFirstButton = true
                                    showLastButton = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun ChildrenBuilder.showPlayers(
    playerList: List<SimplePlayerRecord>,
    listType: String,
    rowsPerPage: Int,
    currentPage: Int
) {
    val tableRows =
        if (rowsPerPage > 0 && playerList.size >= rowsPerPage) {
            val nextEnd = (currentPage * rowsPerPage + rowsPerPage)
            val endpoint = if (nextEnd <= playerList.size) nextEnd else playerList.size
            playerList.slice(currentPage * rowsPerPage until endpoint)
        } else {
            playerList
        }

    val emptyRows =
        if (currentPage > 0) max(0, (1 + currentPage) * rowsPerPage - playerList.size) else 0

    val (tooltips, setTooltip) = useState("")

    tableRows.map {
        TableRow {
            key = it.playerId
            TableCell {
                key = it.playerId
                sx { textAlign = TextAlign.center }
                scope = "row"
                +"${it.first} ${it.last}"
                Tooltip {
                    key = it.playerId
                    title = getPlayerTooltipComponent(it, listType)
                    arrow = true
                    open = tooltips == it.playerId
                    IconButton {
                        onClick = { _ ->
                            if (tooltips == it.playerId) {
                                setTooltip("")
                            } else {
                                setTooltip(it.playerId)
                            }
                        }
                        SportsBaseballTwoTone()
                    }
                }
            }
        }
    }

    if (emptyRows > 0) {
        TableRow {
            sx { height = (33 * emptyRows).px }
            TableCell { colSpan = 5 }
        }
    }
}

fun getPlayerTooltipComponent(record: SimplePlayerRecord, listType: String): ReactElement<*> {
    return FC<PlayerTableProps> {
        val nameDetail = getNameDetails(record, listType)
        Stack {
            Box {
                Link {
                    color = "rgb(133, 206, 237)"
                    target = AnchorTarget._blank
                    href = decorateBbrefId(record.bbrefId, record.playerMgr)
                    underline = LinkUnderline.none
                    +"${record.given} ${record.last}"
                }
            }
            if (nameDetail.isNotEmpty()) {
                Box { +nameDetail }
            }
            Box { +"DOB:  ${record.born}" }
            Box { +"Debut:  ${record.debut}" }
            Box { +"Final game:  ${record.finalGame}" }
            if (record.playerMgr == "1") {
                Box { +"Player Manager" }
            }
        }
    }
        .create()
}

private fun getNameDetails(record: SimplePlayerRecord, listType: String): String {
    return when (listType) {
        "first" -> {
            "First name length: ${record.first.filter { it != ' ' }.length}"
        }
        "last" -> {
            "Last name length: ${record.last.filter { it != ' ' }.length}"
        }
        "firstlast" -> {
            "Given name length: ${(record.first + record.last).filter { it != ' ' }.length}"
        }
        "full" -> {
            "Full name length: ${(record.given + record.last).filter { it != ' '}.length}"
        }
        else -> ""
    }
}
