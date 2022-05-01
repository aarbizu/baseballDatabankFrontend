package org.aarbizu.baseballDatabankFrontend.routes

import csstype.TextAlign
import csstype.em
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mui.material.Box
import mui.material.Button
import mui.material.ButtonGroup
import mui.material.ButtonGroupColor
import mui.material.ButtonGroupVariant
import mui.material.Grid
import mui.material.GridDirection
import mui.material.Size
import mui.material.Stack
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.BasicPlayerList
import org.aarbizu.baseballDatabankFrontend.MinMaxValues
import org.aarbizu.baseballDatabankFrontend.NameLengthInputComponents
import org.aarbizu.baseballDatabankFrontend.PlayerNameLengthParam
import org.aarbizu.baseballDatabankFrontend.SimplePlayerRecord
import org.aarbizu.baseballDatabankFrontend.queryPlayerNameLength
import org.aarbizu.baseballDatabankFrontend.scope
import react.VFC
import react.router.useLocation
import react.useState

val NameLengthSearch = VFC {
    var players by useState(emptyList<SimplePlayerRecord>())
    val loc = useLocation()
    val minMaxValues: MinMaxValues = Json.decodeFromString(loc.state as String)

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

                    NameLengthInputComponents {
                        inputLabel = "Length"
                        onSubmit = { length, option ->
                            scope.launch {
                                players =
                                    queryPlayerNameLength(PlayerNameLengthParam(length, option))
                            }
                        }
                        allowedPattern = { s -> s.isNotEmpty() && s.toIntOrNull() != null }
                        placeHolderString = "10"
                    }

                    Typography {
                        sx { textAlign = TextAlign.right }
                        variant = TypographyVariant.caption
                        +"""
                            Length Ranges:
                                First [${minMaxValues.minFirstName}-${minMaxValues.maxFirstName}] |
                                Last [${minMaxValues.minLastName}-${minMaxValues.maxLastName}] |
                                First+Last [${minMaxValues.minFirstAndLastName}-${minMaxValues.maxFirstAndLastName}] |
                                Full [${minMaxValues.minFullName}-${minMaxValues.maxFullName}]
                        """.trimIndent()
                    }

                    // TODO start using the min/max values in the UI, add buttons to search top N
                    ButtonGroup {
                        variant = ButtonGroupVariant.contained
                        color = ButtonGroupColor.secondary
                        size = Size.small
                        Button { +"Shortest last names" }
                        Button { +"Longest last names" }
                        Button { +"Shortest first names" }
                        Button { +"Shortest last names" }
                    }
                }
            }

            BasicPlayerList { playerList = players }
        }
    }
}
