package org.aarbizu.baseballDatabankFrontend

import csstype.AlignContent
import csstype.Auto
import csstype.em
import csstype.pct
import csstype.px
import kotlinx.js.jso
import mui.material.Link
import mui.material.LinkUnderline
import mui.material.Paper
import mui.material.Size
import mui.material.Table
import mui.material.TableBody
import mui.material.TableCell
import mui.material.TableCellAlign
import mui.material.TableContainer
import mui.material.TableFooter
import mui.material.TableHead
import mui.material.TablePagination
import mui.material.TableRow
import mui.system.sx
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSelectElement
import react.ChildrenBuilder
import react.FC
import react.Props
import react.create
import react.dom.aria.ariaLabel
import react.dom.events.ChangeEventHandler
import react.dom.events.MouseEvent
import react.dom.html.AnchorTarget
import react.dom.html.ReactHTML.th
import react.key
import react.useState
import kotlin.math.max

external interface PlayerTableProps : Props {
    var playerList: List<SimplePlayerRecord>
}

val PlayerTable =
    FC<PlayerTableProps> { props ->
        val (rowsPerPg, setRowsPerPg) = useState(10)
        val (currentPg, setCurrentPg) = useState(0)

        val handleRppChange: ChangeEventHandler<HTMLElement> = { event ->
            //            val jsonObj = event.target.asJsObject()
            //            console.log(event)
            //            console.log(event.target)
            val select = event.target as HTMLSelectElement
            //            val rpp = Object.entries(jsonObj)[0].component2()
            setRowsPerPg(select.value.toInt())
            setCurrentPg(0)
        }
        val handlePgChange: (MouseEvent<HTMLButtonElement, *>?, Number) -> Unit = { _, pageNumber ->
            setCurrentPg(pageNumber as Int)
        }

        TableContainer {
            component = Paper.create().type
            sx {
                alignContent = AlignContent.center
                marginLeft = Auto.auto
                marginRight = Auto.auto
                padding = 1.em
                width = 70.pct
            }
            Table {
                sx { minWidth = 650.px }
                size = Size.small
                ariaLabel = "players"
                TableHead {
                    TableRow {
                        TableCell { +"Name" }
                        TableCell {
                            align = TableCellAlign.right
                            +"bbref"
                        }
                        TableCell {
                            align = TableCellAlign.right
                            +"Born"
                        }
                        TableCell {
                            align = TableCellAlign.right
                            +"Debut"
                        }
                        TableCell {
                            align = TableCellAlign.right
                            +"Final Game"
                        }
                    }
                }

                TableBody { showPlayers(props.playerList, rowsPerPg, currentPg) }

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
                            }
                        }
                    }
                }
            }
        }
    }

fun ChildrenBuilder.showPlayers(
    playerList: List<SimplePlayerRecord>,
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

    tableRows.map {
        TableRow {
            key = it.playerId

            TableCell {
                component = th
                scope = "row"
                +it.name
            }

            TableCell {
                align = TableCellAlign.right
                Link {
                    color = "rgb(0,159,255)"
                    target = AnchorTarget._blank
                    href = decorateBbrefId(it.bbrefId)
                    underline = LinkUnderline.none
                    +it.bbrefId
                }
            }
            TableCell {
                align = TableCellAlign.right
                +it.born
            }
            TableCell {
                align = TableCellAlign.right
                +it.debut
            }
            TableCell {
                align = TableCellAlign.right
                +it.finalGame
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
