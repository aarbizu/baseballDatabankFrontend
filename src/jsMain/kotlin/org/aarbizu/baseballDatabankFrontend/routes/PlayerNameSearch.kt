package org.aarbizu.baseballDatabankFrontend.routes

import csstype.ClassName
import kotlinx.coroutines.launch
import org.aarbizu.baseballDatabankFrontend.NameSearchInput
import org.aarbizu.baseballDatabankFrontend.PlayerTable
import org.aarbizu.baseballDatabankFrontend.SimplePlayerRecord
import org.aarbizu.baseballDatabankFrontend.queryPlayerName
import org.aarbizu.baseballDatabankFrontend.scope
import react.VFC
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.useState

val PlayerNameSearch = VFC {
    var players by useState(emptyList<SimplePlayerRecord>())

    div {
        className = ClassName("content")

        h1 { +"Player Search" }
        +"Search by name fragments or regex, e.g. '.(?:na){2}' -> [Frank Tanana]"
        NameSearchInput {
            textLabel = "Search"
            onSubmit = { input -> scope.launch { players = queryPlayerName(input) } }
            title = "Player name, at last two characters long"
            // allowedPattern = """[a-zA-Z'\- ]{2,}"""
        }

        PlayerTable { playerList = players }
    }
}
