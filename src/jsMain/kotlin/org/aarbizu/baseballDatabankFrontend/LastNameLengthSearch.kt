package org.aarbizu.baseballDatabankFrontend

import kotlinext.js.asJsObject
import kotlinx.coroutines.launch
import react.VFC
import react.dom.html.ReactHTML.h1
import react.router.useLocation
import react.useState
import kotlin.js.Date

val LastNameLengthSearch = VFC {
    var players by useState(emptyList<SimplePlayerRecord>())
    val stateObj = useLocation().state
    val inboundInfo = Date.now()

    h1 { +"Player Search By Last Name Length [$inboundInfo][${stateObj.asJsObject()}]" }

    InputComponent {
        inputLabel = "Player Last Name Length"
        onSubmit = { input ->
            scope.launch { players = queryPlayerNameLength(PlayerNameLengthParam(input)) }
        }
        title = "Last name length, up to two digits"
        allowedPattern = """[0-9]{1,2}"""
    }

    PlayerTable { playerList = players }
}
