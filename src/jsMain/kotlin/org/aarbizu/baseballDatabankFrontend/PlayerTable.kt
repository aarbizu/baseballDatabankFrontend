package org.aarbizu.baseballDatabankFrontend

import react.ChildrenBuilder
import react.FC
import react.Props
import react.dom.html.AnchorTarget
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.tbody
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.tr

external interface PlayerTableProps : Props {
    var playerList: List<SimplePlayerRecord>
}

val PlayerTable =
    FC<PlayerTableProps> { props -> table { tbody { showPlayers(props.playerList) } } }

fun ChildrenBuilder.showPlayers(playerList: List<SimplePlayerRecord>) {
    playerList.forEach {
        tr {
            td { +it.name }
            td { +it.born }
            td { +it.debut }
            td { +it.finalGame }
            td {
                a {
                    target = AnchorTarget._blank
                    href = decorateBbrefId(it.bbrefId)
                    +it.bbrefId
                }
            }
        }
    }
}
