package org.aarbizu.baseballDatabankFrontend.routes

import csstype.TextAlign
import csstype.em
import csstype.px
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
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
import org.aarbizu.baseballDatabankFrontend.getPitchingCareerStats
import org.aarbizu.baseballDatabankFrontend.isSuperVocalic
import org.aarbizu.baseballDatabankFrontend.scope
import react.ChildrenBuilder
import react.FC
import react.ReactElement
import react.ReactNode
import react.StateSetter
import react.VFC
import react.create
import react.dom.html.AnchorTarget
import react.key
import react.router.useLocation
import react.useState

val StatsSearch = VFC {
    val loc = useLocation()
    var playerStats by useState(emptyList<PlayerCareerStatRecord>())
    var selectedStat by useState("")
    val statNames: StatNames = Json.decodeFromString(loc.state as String)

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
                            label = ReactNode("Hitting Stat")
                            onChange = { event, _ -> selectedStat = event.target.value }
                            statNames.hitting.forEach {
                                MenuItem {
                                    value = it
                                    +it
                                }
                            }
                        }
                        Button {
                            size = Size.small
                            onClick = {
                                if (selectedStat.isNotBlank() || selectedStat.isNotEmpty()) {
                                    scope.launch {
                                        playerStats = getOffenseCareerStats(StatParam(selectedStat))
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
                            value = selectedStat.unsafeCast<Nothing?>()
                            label = ReactNode("Pitching Stat")
                            onChange = { event, _ -> selectedStat = event.target.value }
                            statNames.pitching.forEach {
                                MenuItem {
                                    value = it
                                    +it
                                }
                            }
                        }
                        Button {
                            size = Size.small
                            onClick = {
                                if (selectedStat.isNotBlank() || selectedStat.isNotEmpty()) {
                                    scope.launch {
                                        playerStats =
                                            getPitchingCareerStats(StatParam(selectedStat))
                                    }
                                }
                            }
                            +"Get Leaders"
                        }
                    }

                    Button {
                        size = Size.small
                        onClick = { scope.launch { playerStats = keepSupervocalics(playerStats) } }
                        +"Only Supervocalic"
                    }
                }
            }

            CareerStatList { playerStatList = playerStats }
        }
    }
}

fun keepSupervocalics(players: List<PlayerCareerStatRecord>): List<PlayerCareerStatRecord> {
    return players.filter { isSuperVocalic(it.name) }
}

fun ChildrenBuilder.showPlayerStats(
    playerList: List<BaseballRecord>,
    pagination: PaginationControls
) {
    val tableRows =
        getPageBoundaries(playerList, pagination.rowsPerPage, pagination.currentPage)
            as List<PlayerCareerStatRecord>
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
    setTooltip: StateSetter<String>
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
    setTooltip: StateSetter<String>
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
                    target = AnchorTarget._blank
                    href = decorateBbrefId(id, "0")
                    underline = LinkUnderline.none
                    +"see $name on bbref"
                }
            }
        }
    }
        .create()
}
