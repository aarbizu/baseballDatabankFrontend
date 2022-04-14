package org.aarbizu.baseballDatabankFrontend

import kotlinx.coroutines.launch
import react.FC
import react.Props
import react.dom.html.AnchorTarget
import react.dom.html.ReactHTML
import react.useState

val LastNameSearch =
    FC<Props> {
        var players by useState(emptyList<SimplePlayerRecord>())

        ReactHTML.h1 { +"Baseball Databank" }

        InputComponent {
            inputLabel = "Player Last Name Length"
            onSubmit = { input ->
                scope.launch { players = queryPlayerNameLength(PlayerNameLengthParam(input)) }
            }
            title = "Last name length, up to two digits"
            allowedPattern = "[0-9]{1,2}"
        }

        ReactHTML.table {
            ReactHTML.tbody {
                players.forEach {
                    ReactHTML.tr {
                        ReactHTML.td { +it.name }
                        ReactHTML.td { +it.born }
                        ReactHTML.td { +it.debut }
                        ReactHTML.td { +it.finalGame }
                        ReactHTML.td {
                            ReactHTML.a {
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
