package org.aarbizu.baseballDatabankFrontend.routes

import csstype.TextAlign
import csstype.em
import csstype.px
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mui.material.Box
import mui.material.Button
import mui.material.FormControl
import mui.material.FormControlVariant
import mui.material.Grid
import mui.material.GridDirection
import mui.material.InputLabel
import mui.material.MenuItem
import mui.material.Select
import mui.material.Size
import mui.material.Stack
import mui.material.TableCell
import mui.material.TableRow
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.BaseballRecord
import org.aarbizu.baseballDatabankFrontend.CareerStatList
import org.aarbizu.baseballDatabankFrontend.OffenseStatParam
import org.aarbizu.baseballDatabankFrontend.PaginationControls
import org.aarbizu.baseballDatabankFrontend.PlayerCareerStatRecord
import org.aarbizu.baseballDatabankFrontend.countOfEmptyRows
import org.aarbizu.baseballDatabankFrontend.getOffenseCareerStats
import org.aarbizu.baseballDatabankFrontend.getPageBoundaries
import org.aarbizu.baseballDatabankFrontend.scope
import react.ChildrenBuilder
import react.ReactNode
import react.VFC
import react.key
import react.router.useLocation
import react.useState

val StatsSearch = VFC {
    val loc = useLocation()
    var playerStats by useState(emptyList<PlayerCareerStatRecord>())
    var selectedStat by useState("")
    val statNames: List<String> = Json.decodeFromString(loc.state as String)

    Box {
        sx { padding = 1.em }

        Grid {
            container = true
            direction = responsive(GridDirection.row)

            Grid {
                md = 6
                xs = 12
                item = true
                container = true
                direction = responsive(GridDirection.column)

                Stack {
                    Typography {
                        variant = TypographyVariant.body1
                        sx {
                            textAlign = TextAlign.right
                            paddingBottom = 0.1.em
                        }
                        +"Stats"
                    }
                    FormControl {
                        sx { margin = 1.em }
                        variant = FormControlVariant.standard
                        size = Size.small
                        InputLabel {
                            id = "hit-stats-select"
                            +"Hitting Stat"
                        }
                        Select {
                            labelId = "hit-stats-select"
                            value = selectedStat.unsafeCast<Nothing?>()
                            label = ReactNode("Stat")
                            onChange = { event, _ -> selectedStat = event.target.value }
                            statNames.forEach {
                                MenuItem {
                                    value = it
                                    +it
                                }
                            }
                        }
                        Button {
                            size = Size.small
                            onClick = {
                                scope.launch {
                                    playerStats =
                                        getOffenseCareerStats(OffenseStatParam(selectedStat))
                                }
                            }
                            +"Get Leaders"
                        }
                    }
                }
            }

            CareerStatList { playerStatList = playerStats }
        }
    }
}

fun ChildrenBuilder.showPlayerStats(
    playerList: List<BaseballRecord>,
    pagination: PaginationControls
) {
    val tableRows =
        getPageBoundaries(playerList, pagination.rowsPerPage, pagination.currentPage)
            as List<PlayerCareerStatRecord>
    val emptyRows = countOfEmptyRows(playerList, pagination.rowsPerPage, pagination.currentPage)
    //    val (tooltips, setTooltip) = useState("")

    tableRows.map {
        TableRow {
            key = it.id
            TableCell {
                key = it.id + "-name"
                sx { textAlign = TextAlign.center }
                scope = "row"
                +it.name
            }
            TableCell {
                key = it.id + "-stat-value"
                sx { textAlign = TextAlign.center }
                scope = "row"
                +it.stat
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
