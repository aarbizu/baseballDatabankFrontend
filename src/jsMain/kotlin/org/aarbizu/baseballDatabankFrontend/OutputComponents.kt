package org.aarbizu.baseballDatabankFrontend

import csstype.AlignItems
import csstype.JustifyContent
import csstype.em
import mui.material.Grid
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.routes.showPlayerStats
import react.FC
import react.Props

external interface BasicPlayerListProps : Props {
    var playerList: List<BaseballRecord>
    var listType: String
}

val BasicPlayerList =
    FC<BasicPlayerListProps> { props ->
        val rowsPerPage = 10
        val currentPage = 0
        val paginationControls = getPaginationControls(rowsPerPage, currentPage)
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
                    pagination = { it.showPageControls(playerList.size, paginationControls) }
                    tableDataRenderer = { it.showPlayers(playerList, listType, paginationControls) }
                }
            } else {
                NoPlayersFound {}
            }
        }
    }

external interface CareerStatListProps : Props {
    var playerStatList: List<PlayerCareerStatRecord>
    var paginationControls: PaginationControls
}

val CareerStatList =
    FC<CareerStatListProps> { props ->
        Grid {
            item = true
            md = 6
            xs = 12
            sx {
                paddingTop = 0.25.em
                alignItems = AlignItems.center
                justifyContent = JustifyContent.left
            }
            if (props.playerStatList.isNotEmpty()) {
                PlayerTable {
                    ariaLabelString = "career-stats"
                    playerList = props.playerStatList
                    listType = "career-stats"
                    pagination = { it.showPageControls(playerList.size, props.paginationControls) }
                    tableDataRenderer = { it.showPlayerStats(playerList, props.paginationControls) }
                }
            } else {
                NoPlayersFound {}
            }
        }
    }
