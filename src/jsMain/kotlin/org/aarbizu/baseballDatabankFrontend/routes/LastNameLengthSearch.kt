package org.aarbizu.baseballDatabankFrontend.routes

import csstype.ClassName
import kotlinx.coroutines.launch
import org.aarbizu.baseballDatabankFrontend.InputComponent
import org.aarbizu.baseballDatabankFrontend.PlayerNameLengthParam
import org.aarbizu.baseballDatabankFrontend.PlayerTable
import org.aarbizu.baseballDatabankFrontend.SimplePlayerRecord
import org.aarbizu.baseballDatabankFrontend.queryPlayerNameLength
import org.aarbizu.baseballDatabankFrontend.scope
import react.VFC
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.useState

val LastNameLengthSearch = VFC {
    var players by useState(emptyList<SimplePlayerRecord>())

    div {
        className = ClassName("content")

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
}