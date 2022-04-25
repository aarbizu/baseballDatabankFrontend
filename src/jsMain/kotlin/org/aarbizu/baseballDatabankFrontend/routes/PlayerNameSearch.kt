package org.aarbizu.baseballDatabankFrontend.routes

import csstype.Auto
import csstype.NamedColor
import csstype.Padding
import csstype.em
import csstype.pct
import kotlinx.coroutines.launch
import mui.material.Box
import mui.material.MuiList.Companion.padding
import mui.material.Stack
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.NameSearchInput
import org.aarbizu.baseballDatabankFrontend.NoPlayersFound
import org.aarbizu.baseballDatabankFrontend.PlayerTable
import org.aarbizu.baseballDatabankFrontend.SimplePlayerRecord
import org.aarbizu.baseballDatabankFrontend.queryPlayerName
import org.aarbizu.baseballDatabankFrontend.scope
import react.VFC
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.em
import react.useState

val PlayerNameSearch = VFC {
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
                +"Player Search"
            }

            Typography {
                sx {
                    padding = 0.1.em
                    marginLeft = Auto.auto
                    marginRight = Auto.auto
                    width = 70.pct
                }
                variant = TypographyVariant.body1
                +"""
                    Search by partial name match or regex
                """.trimIndent()
            }
            Typography {
                sx {
                    padding = Padding(0.1.em, 0.1.em, 0.1.em, 0.5.em)
                    marginLeft = Auto.auto
                    marginRight = Auto.auto
                    width = 70.pct
                    color = NamedColor.darkgrey
                }
                variant = TypographyVariant.caption
                +"""
                   e.g., partial: "Bond" | regex: ".(?:na){2}" (matches [Frank Tanana], et al.) 
                """.trimIndent()
            }

            Box { sx { padding = 1.em } }

            NameSearchInput {
                textLabel = "Search"
                onSubmit = { input -> scope.launch { players = queryPlayerName(input) } }
                title = "Player name search string"
            }

            if (players.isNotEmpty()) {
                PlayerTable { playerList = players }
            } else {
                NoPlayersFound {}
            }
        }
    }
}
