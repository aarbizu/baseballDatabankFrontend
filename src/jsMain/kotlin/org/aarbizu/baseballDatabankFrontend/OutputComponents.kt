package org.aarbizu.baseballDatabankFrontend

import csstype.AlignItems
import csstype.JustifyContent
import csstype.em
import mui.material.Grid
import mui.system.sx
import react.FC
import react.Props

external interface BasicPlayerListProps : Props {
    var playerList: List<SimplePlayerRecord>
    var listType: String
}

val BasicPlayerList =
    FC<BasicPlayerListProps> { props ->
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
                    playerList = props.playerList
                    listType = props.listType
                }
            } else {
                NoPlayersFound {}
            }
        }
    }
