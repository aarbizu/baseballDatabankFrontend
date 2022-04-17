package org.aarbizu.baseballDatabankFrontend

import kotlinx.coroutines.launch
import react.VFC
import react.dom.html.ReactHTML.h1
import react.useState

val PlayerNameSearch = VFC {
    var players by useState(emptyList<SimplePlayerRecord>())

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
