package org.aarbizu.baseballDatabankFrontend.routes

import csstype.TextAlign
import csstype.em
import kotlinx.coroutines.launch
import mui.material.Box
import mui.material.Grid
import mui.material.GridDirection
import mui.material.Stack
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.BasicPlayerList
import org.aarbizu.baseballDatabankFrontend.PlayerNameLengthParam
import org.aarbizu.baseballDatabankFrontend.SimplePlayerRecord
import org.aarbizu.baseballDatabankFrontend.TextInputComponent
import org.aarbizu.baseballDatabankFrontend.queryPlayerNameLength
import org.aarbizu.baseballDatabankFrontend.scope
import react.VFC
import react.useState

val NameLengthSearch = VFC {
    var players by useState(emptyList<SimplePlayerRecord>())

    Box {
        sx { padding = 1.em }

        Grid {
            container = true
            direction = responsive(GridDirection.row)

            Grid {
                xl = 6
                sm = 12
                item = true
                container = true
                direction = responsive(GridDirection.column)

                Stack {
                    Typography {
                        variant = TypographyVariant.h6
                        sx { textAlign = TextAlign.right }
                        +"Name Length"
                    }

                    TextInputComponent {
                        inputLabel = "Length"
                        onSubmit = { input ->
                            scope.launch {
                                players = queryPlayerNameLength(PlayerNameLengthParam(input))
                            }
                        }
                        title = "Last name length, up to two digits"
                        allowedPattern = { s -> s.isNotEmpty() && s.toIntOrNull() != null }
                        placeHolderString = "10"
                    }
                }
            }

            BasicPlayerList { playerList = players }
        }
    }
}
