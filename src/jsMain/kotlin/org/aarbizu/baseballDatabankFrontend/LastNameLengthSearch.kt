package org.aarbizu.baseballDatabankFrontend

import kotlinx.coroutines.launch
import react.FC
import react.Props
import react.dom.html.AnchorTarget
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.tbody
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.tr
import react.useState

val LastNameLengthSearch =
    FC<Props> {
        var players by useState(emptyList<SimplePlayerRecord>())

        h1 { +"Player Search By Last Name Length" }

        InputComponent {
            inputLabel = "Player Last Name Length"
            onSubmit = { input ->
                scope.launch { players = queryPlayerNameLength(PlayerNameLengthParam(input)) }
            }
            title = "Last name length, up to two digits"
            allowedPattern = "[0-9]{1,2}"
        }
        // TODO - make this a component
        table {
            tbody {
                players.forEach {
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
        }
    }
