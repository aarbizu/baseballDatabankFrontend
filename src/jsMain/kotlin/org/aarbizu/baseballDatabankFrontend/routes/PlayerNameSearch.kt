package org.aarbizu.baseballDatabankFrontend.routes

import csstype.FontStyle
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
import org.aarbizu.baseballDatabankFrontend.NameSearchInput
import org.aarbizu.baseballDatabankFrontend.SimplePlayerRecord
import org.aarbizu.baseballDatabankFrontend.myAppTheme
import org.aarbizu.baseballDatabankFrontend.queryPlayerName
import org.aarbizu.baseballDatabankFrontend.scope
import react.VFC
import react.useState

val PlayerNameSearch = VFC {
    var players by useState(emptyList<SimplePlayerRecord>())

    Box {
        sx { padding = 1.em }

        Grid {
            container = true
            direction = responsive(GridDirection.row)

            Grid {
                md = 6
                xs = 12
                item = true
                container = true
                direction = responsive(GridDirection.column)

                Stack {
                    Typography {
                        variant = TypographyVariant.h6
                        sx { textAlign = TextAlign.right }
                        +"Name"
                    }

                    Typography {
                        variant = TypographyVariant.body1
                        sx {
                            textAlign = TextAlign.right
                            paddingBottom = 0.1.em
                        }
                        +"""
                        Search by partial name match or regex
                        """.trimIndent()
                    }

                    Typography {
                        variant = TypographyVariant.caption
                        sx {
                            textAlign = TextAlign.right
                            color = myAppTheme.palette.text.secondary
                            fontStyle = FontStyle.italic
                            paddingTop = 0.1.em
                        }
                        +"""
                        [ e.g., partial: "Bond" | regex: ".(?:na){2}" ]
                        """.trimIndent()
                    }

                    NameSearchInput {
                        textLabel = "Search"
                        onSubmit = { input -> scope.launch { players = queryPlayerName(input) } }
                    }
                }
            }

            BasicPlayerList { playerList = players }
        }
    }
}
