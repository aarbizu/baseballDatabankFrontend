package org.aarbizu.baseballDatabankFrontend.routes

import csstype.Auto
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
import mui.material.FormControl
import mui.material.Grid
import mui.material.GridDirection
import mui.material.InputLabel
import mui.material.MenuItem
import mui.material.Select
import mui.material.Size
import mui.material.Stack
import mui.material.StackDirection
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.BasicPlayerList
import org.aarbizu.baseballDatabankFrontend.MinMaxValues
import org.aarbizu.baseballDatabankFrontend.NameLengthInputComponents
import org.aarbizu.baseballDatabankFrontend.NamesSortedByLengthParam
import org.aarbizu.baseballDatabankFrontend.PlayerNameLengthParam
import org.aarbizu.baseballDatabankFrontend.SimplePlayerRecord
import org.aarbizu.baseballDatabankFrontend.getSortedNames
import org.aarbizu.baseballDatabankFrontend.queryPlayerNameLength
import org.aarbizu.baseballDatabankFrontend.scope
import react.ReactNode
import react.VFC
import react.router.useLocation
import react.useState

val NameLengthSearch = VFC {
    var players by useState(emptyList<SimplePlayerRecord>())
    var topNParam by useState("10")
    val loc = useLocation()
    val minMaxValues: MinMaxValues = Json.decodeFromString(loc.state as String)

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

                    Stack {
                        sx {
                            marginLeft = Auto.auto
                            paddingTop = 1.em
                        }
                        direction = responsive(StackDirection.row)
                        ButtonGroup {
                            variant = ButtonGroupVariant.contained
                            color = ButtonGroupColor.secondary
                            Button {
                                size = Size.small
                                onClick = {
                                    scope.launch {
                                        players =
                                            getSortedNames(
                                                NamesSortedByLengthParam(
                                                    type = "last",
                                                    descending = "true",
                                                    topN = topNParam
                                                )
                                            )
                                    }
                                }
                                +"Longest last names"
                            }
                            Button {
                                size = Size.small
                                +"N last names"
                            }
                            Button {
                                size = Size.small
                                +"TopN first names"
                            }
                            Button {
                                size = Size.small
                                +"BottomN last names"
                            }
                        }
                        FormControl {
                            fullWidth = false
                            InputLabel {
                                id = "top-name-select-label"
                                +"TopN"
                            }
                            Select {
                                labelId = "top-name-select-label"
                                id = "top-name-select"
                                value = topNParam.unsafeCast<Nothing?>()
                                label = ReactNode("TopN")
                                onChange = { event, _ -> topNParam = event.target.value }
                                listOf("10", "25", "50", "100").forEach {
                                    MenuItem {
                                        value = it
                                        +it
                                    }
                                }
                            }
                        }
                    }
                }
            }

            BasicPlayerList { playerList = players }
        }
    }
}
