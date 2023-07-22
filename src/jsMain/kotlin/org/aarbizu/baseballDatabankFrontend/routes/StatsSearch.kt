package org.aarbizu.baseballDatabankFrontend.routes

import kotlinx.coroutines.launch
import mui.icons.material.SportsBaseballTwoTone
import mui.material.Box
import mui.material.Button
import mui.material.FormControl
import mui.material.FormControlVariant
import mui.material.Grid
import mui.material.GridDirection
import mui.material.IconButton
import mui.material.InputLabel
import mui.material.Link
import mui.material.LinkUnderline
import mui.material.MenuItem
import mui.material.Select
import mui.material.Size
import mui.material.Stack
import mui.material.TableCell
import mui.material.TableRow
import mui.material.Tooltip
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.BaseballRecord
import org.aarbizu.baseballDatabankFrontend.CareerStatList
import org.aarbizu.baseballDatabankFrontend.PaginationControls
import org.aarbizu.baseballDatabankFrontend.PlayerCareerStatRecord
import org.aarbizu.baseballDatabankFrontend.StatNames
import org.aarbizu.baseballDatabankFrontend.StatParam
import org.aarbizu.baseballDatabankFrontend.TableProps
import org.aarbizu.baseballDatabankFrontend.countOfEmptyRows
import org.aarbizu.baseballDatabankFrontend.decorateBbrefId
import org.aarbizu.baseballDatabankFrontend.getOffenseCareerStats
import org.aarbizu.baseballDatabankFrontend.getPageBoundaries
import org.aarbizu.baseballDatabankFrontend.getPaginationControls
import org.aarbizu.baseballDatabankFrontend.getPitchingCareerStats
import org.aarbizu.baseballDatabankFrontend.isSuperVocalic
import org.aarbizu.baseballDatabankFrontend.md
import org.aarbizu.baseballDatabankFrontend.scope
import org.aarbizu.baseballDatabankFrontend.store
import org.aarbizu.baseballDatabankFrontend.xs
import react.ChildrenBuilder
import react.FC
import react.ReactElement
import react.ReactNode
import react.StateSetter
import react.VFC
import react.create
import react.useState
import web.cssom.TextAlign
import web.cssom.em
import web.cssom.px
import web.window.WindowTarget

val StatsSearch = VFC {
    var playerStats by useState(emptyList<PlayerCareerStatRecord>())
    var selectedHittingStat by useState("")
    var selectedPitchingStat by useState("")
    val statNames = StatNames(store.getState().hittingStateNames, store.getState().pitchingStatNames)
    val pagination = getPaginationControls(10, 0)

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
                            value = selectedHittingStat.unsafeCast<Nothing?>()
                            label = ReactNode("Hitting Stat")
                            onChange = { event, _ -> selectedHittingStat = event.target.value }
                            statNames.hitting.forEach {
                                MenuItem {
                                    value = it.trim()
                                    +it.trim()
                                }
                            }
                        }
                        Button {
                            size = Size.small
                            onClick = {
                                if (
                                    selectedHittingStat.isNotBlank() ||
                                    selectedHittingStat.isNotEmpty()
                                ) {
                                    scope.launch {
                                        pagination.setCurrentPg(0)
                                        playerStats =
                                            getOffenseCareerStats(StatParam(selectedHittingStat))
                                    }
                                }
                            }
                            +"Get Leaders"
                        }
                    }

                    FormControl {
                        sx { margin = 1.em }
                        variant = FormControlVariant.standard
                        size = Size.small
                        InputLabel {
                            id = "pitch-stats-select"
                            +"Pitching Stat"
                        }
                        Select {
                            labelId = "pitch-stats-select"
                            value = selectedPitchingStat.unsafeCast<Nothing?>()
                            label = ReactNode("Pitching Stat")
                            onChange = { event, _ -> selectedPitchingStat = event.target.value }
                            statNames.pitching.forEach {
                                MenuItem {
                                    value = it.trim()
                                    +it.trim()
                                }
                            }
                        }
                        Button {
                            size = Size.small
                            onClick = {
                                if (
                                    selectedPitchingStat.isNotBlank() ||
                                    selectedPitchingStat.isNotEmpty()
                                ) {
                                    scope.launch {
                                        pagination.setCurrentPg(0)
                                        playerStats =
                                            getPitchingCareerStats(StatParam(selectedPitchingStat))
                                    }
                                }
                            }
                            +"Get Leaders"
                        }
                    }

                    Button {
                        size = Size.small
                        onClick = {
                            scope.launch {
                                pagination.setCurrentPg(0)
                                playerStats = keepSupervocalics(playerStats)
                            }
                        }
                        +"Only Supervocalic"
                    }
                }
            }

            CareerStatList {
                playerList = playerStats
                paginationControls = pagination
            }
        }
    }
}

fun keepSupervocalics(players: List<PlayerCareerStatRecord>): List<PlayerCareerStatRecord> {
    return players.filter { it.name.isSuperVocalic() }
}

fun ChildrenBuilder.showPlayerStats(
    playerList: List<BaseballRecord>,
    pagination: PaginationControls,
) {
    val tableRows =
        getPageBoundaries(playerList, pagination.rowsPerPage, pagination.currentPage)
            .unsafeCast<List<PlayerCareerStatRecord>>()
    val emptyRows = countOfEmptyRows(playerList, pagination.rowsPerPage, pagination.currentPage)
    val (tooltip, setTooltip) = useState("")

    tableRows.map {
        TableRow {
            key = it.id
            TableCell {
                key = it.id + "-name"
                sx { textAlign = TextAlign.center }
                scope = "row"
                +it.name
                getTooltip(it, tooltip, setTooltip)
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

fun ChildrenBuilder.getTooltip(
    record: PlayerCareerStatRecord,
    tooltip: String,
    setTooltip: StateSetter<String>,
) {
    Tooltip {
        key = record.id
        title = getBasicToolTip(record.id, record.name)
        arrow = true
        open = tooltip == record.id
        tooltipButton(record.id, tooltip, setTooltip)
    }
}

fun ChildrenBuilder.tooltipButton(
    id: String,
    tooltipString: String,
    setTooltip: StateSetter<String>,
) {
    IconButton {
        onClick = { _ ->
            if (tooltipString == id) {
                setTooltip("")
            } else {
                setTooltip(id)
            }
        }
        SportsBaseballTwoTone()
    }
}

fun getBasicToolTip(id: String, name: String): ReactElement<*> {
    return FC<TableProps> {
        Stack {
            Box {
                Link {
                    color = "rgb(133, 206, 237)"
                    target = WindowTarget._blank
                    href = decorateBbrefId(id, "0")
                    underline = LinkUnderline.none
                    +"see $name on bbref"
                }
            }
        }
    }
        .create()
}
