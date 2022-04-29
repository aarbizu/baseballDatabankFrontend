package org.aarbizu.baseballDatabankFrontend

import csstype.AlignItems
import csstype.JustifyContent
import mui.material.Grid
import mui.system.sx
import react.FC
import react.Props

external interface BasicPlayerListProps : Props {
    var playerList: List<SimplePlayerRecord>
}

val BasicPlayerList =
    FC<BasicPlayerListProps> { props ->
        Grid {
            item = true
            xl = 6
            sm = 12
            sx {
                alignItems = AlignItems.center
                justifyContent = JustifyContent.left
            }
            if (props.playerList.isNotEmpty()) {
                PlayerTable { playerList = props.playerList }
            } else {
                NoPlayersFound {}
            }
        }
    }
