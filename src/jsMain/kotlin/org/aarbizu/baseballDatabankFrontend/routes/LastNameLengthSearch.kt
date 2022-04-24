package org.aarbizu.baseballDatabankFrontend.routes

import csstype.Auto
import csstype.em
import csstype.pct
import kotlinx.coroutines.launch
import mui.material.Box
import mui.material.Stack
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.PlayerNameLengthParam
import org.aarbizu.baseballDatabankFrontend.PlayerTable
import org.aarbizu.baseballDatabankFrontend.SimplePlayerRecord
import org.aarbizu.baseballDatabankFrontend.TextInputComponent
import org.aarbizu.baseballDatabankFrontend.queryPlayerNameLength
import org.aarbizu.baseballDatabankFrontend.scope
import react.VFC
import react.dom.html.ReactHTML.div
import react.useState

val LastNameLengthSearch = VFC {
    var players by useState(emptyList<SimplePlayerRecord>())
    Box {
        component = div
        sx {
            padding = 0.8.em
            marginLeft = Auto.auto
            marginRight = Auto.auto
            width = 70.pct
        }

        Stack {
            Typography {
                sx {
                    padding = 0.2.em
                    marginLeft = Auto.auto
                    marginRight = Auto.auto
                    width = 70.pct
                }
                variant = TypographyVariant.h6
                +"Player Search By Name Length"
            }

            TextInputComponent {
                inputLabel = "Length"
                onSubmit = { input ->
                    scope.launch { players = queryPlayerNameLength(PlayerNameLengthParam(input)) }
                }
                title = "Last name length, up to two digits"
                allowedPattern = { s -> s.isNotEmpty() && s.toIntOrNull() != null }
                placeHolderString = "10"
            }
        }

        if (players.isNotEmpty()) {
            PlayerTable { playerList = players }
        } else {
            Box {
                component = div
                Typography {
                    sx {
                        padding = 0.2.em
                        marginLeft = Auto.auto
                        marginRight = Auto.auto
                        width = 70.pct
                    }
                    variant = TypographyVariant.body1
                    +"No players found"
                }
            }
        }
    }
}
