package org.aarbizu.baseballDatabankFrontend

import csstype.TextAlign
import csstype.pct
import csstype.px
import mui.material.Box
import mui.material.Container
import mui.material.Link
import mui.material.LinkUnderline
import mui.material.Paper
import mui.material.Stack
import mui.material.Table
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableContainer
import mui.material.TableRow
import mui.material.Tooltip
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.routes.tooltipButton
import react.ChildrenBuilder
import react.FC
import react.Props
import react.ReactElement
import react.create
import react.dom.aria.ariaLabel
import react.useState
import web.window.WindowTarget
import kotlin.math.max

external interface TableProps : Props {
    var playerList: List<BaseballRecord>
    var listType: String
    var ariaLabelString: String
    var pagination: (ChildrenBuilder) -> Unit
    var tableDataRenderer: (ChildrenBuilder) -> Unit
}

fun getPaginationControls(rowsPerPage: Int, currentPage: Int): PaginationControls {
    val (rowsPerPg, setRowsPerPg) = useState(rowsPerPage)
    val (currentPg, setCurrentPg) = useState(currentPage)
    return PaginationControls(rowsPerPg, setRowsPerPg, currentPg, setCurrentPg)
}

val PlayerTable =
    FC<TableProps> { props ->
        Container {
            sx { width = 100.pct }
            TableContainer {
                component = Paper.create().type
                Table {
                    ariaLabel = props.ariaLabelString
                    TableBody { props.tableDataRenderer(this) }
                    if (props.playerList.size > 10) {
                        props.pagination(this)
                    }
                }
            }
        }
    }

fun ChildrenBuilder.showPlayers(
    playerList: List<BaseballRecord>,
    listType: String,
    pagination: PaginationControls,
) {
    val tableRows =
        getPageBoundaries(playerList, pagination.rowsPerPage, pagination.currentPage)
            .unsafeCast<List<SimplePlayerRecord>>()
    val emptyRows = countOfEmptyRows(playerList, pagination.rowsPerPage, pagination.currentPage)
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
                    tooltipButton(it.playerId, tooltips, setTooltip)
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

fun countOfEmptyRows(records: List<*>, rowsPerPage: Int, currentPage: Int) =
    if (currentPage > 0) max(0, (1 + currentPage) * rowsPerPage - records.size) else 0

fun getPageBoundaries(records: List<BaseballRecord>, rowsPerPage: Int, currentPage: Int) =
    if (rowsPerPage > 0 && records.size >= rowsPerPage) {
        val nextEnd = (currentPage * rowsPerPage + rowsPerPage)
        val endpoint = if (nextEnd <= records.size) nextEnd else records.size
        records.slice(currentPage * rowsPerPage until endpoint)
    } else {
        records
    }

fun getPlayerTooltipComponent(record: SimplePlayerRecord, listType: String): ReactElement<*> {
    return FC<TableProps> {
        val nameDetail = getNameDetails(record, listType)
        Stack {
            Box {
                Link {
                    color = "rgb(133, 206, 237)"
                    target = WindowTarget._blank
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

fun decorateBbrefId(bbrefid: String, playerMgr: String): String {
    return if (playerMgr == "1") {
        "$bbrefUri/managers/$bbrefid$bbrefSuffix"
    } else {
        "$bbrefUri/players/${bbrefid[0]}/$bbrefid$bbrefSuffix"
    }
}
