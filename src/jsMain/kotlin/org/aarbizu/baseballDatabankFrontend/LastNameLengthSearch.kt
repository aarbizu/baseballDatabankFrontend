package org.aarbizu.baseballDatabankFrontend

import kotlinx.coroutines.launch
import react.VFC
import react.dom.html.ReactHTML.h1
import react.useState

val LastNameLengthSearch = VFC {
    var players by useState(emptyList<SimplePlayerRecord>())

    h1 { +"Player Search By Name Length" }

    InputComponent {
        inputLabel = "Length"
        onSubmit = { input ->
            scope.launch { players = queryPlayerNameLength(PlayerNameLengthParam(input)) }
        }
        title = "Last name length, up to two digits"
        allowedPattern = """[0-9]{1,2}"""
    }

    PlayerTable { playerList = players }
}
