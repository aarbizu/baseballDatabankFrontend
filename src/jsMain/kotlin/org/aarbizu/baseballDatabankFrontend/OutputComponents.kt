package org.aarbizu.baseballDatabankFrontend

import csstype.AlignItems
import csstype.JustifyContent
import csstype.em
import mui.material.Grid
import mui.material.GridProps
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.routes.showPlayerStats
import react.FC
import react.Props

external interface PlayerListProps : Props {
    var playerList: List<BaseballRecord>
    var paginationControls: PaginationControls
    var listType: String
}

inline var GridProps.xs: Any?
    get() = asDynamic().xs
    set(value) { asDynamic().xs = value }

inline var GridProps.md: Any?
    get() = asDynamic().md
    set(value) { asDynamic().md = value }

val BasicPlayerList =
    FC<PlayerListProps> { props ->
        Grid {
            item = true
            md = 6
            xs = 12
            sx {
                paddingTop = 0.25.em
                alignItems = AlignItems.center
                justifyContent = JustifyContent.left
            }
            if (props.playerList.isNotEmpty()) {
                PlayerTable {
                    ariaLabelString = "players"
                    playerList = props.playerList
                    listType = props.listType
                    pagination = { it.showPageControls(playerList.size, props.paginationControls) }
                    tableDataRenderer = {
                        it.showPlayers(playerList, listType, props.paginationControls)
                    }
                }
            } else {
                NoPlayersFound {}
            }
        }
    }

val CareerStatList =
    FC<PlayerListProps> { props ->
        Grid {
            item = true
            md = 6
            xs = 12
            sx {
                paddingTop = 0.25.em
                alignItems = AlignItems.center
                justifyContent = JustifyContent.left
            }
            if (props.playerList.isNotEmpty()) {
                PlayerTable {
                    ariaLabelString = "career-stats"
                    playerList = props.playerList
                    listType = "career-stats"
                    pagination = { it.showPageControls(playerList.size, props.paginationControls) }
                    tableDataRenderer = { it.showPlayerStats(playerList, props.paginationControls) }
                }
            } else {
                NoPlayersFound {}
            }
        }
    }
