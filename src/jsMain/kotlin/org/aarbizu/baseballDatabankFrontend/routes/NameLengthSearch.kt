package org.aarbizu.baseballDatabankFrontend.routes

import csstype.Auto
import csstype.TextAlign
import csstype.em
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mui.icons.material.ArrowDownward
import mui.icons.material.ArrowUpward
import mui.material.Box
import mui.material.Button
import mui.material.ButtonGroup
import mui.material.ButtonGroupColor
import mui.material.ButtonGroupVariant
import mui.material.FormControl
import mui.material.Grid
import mui.material.GridDirection
import mui.material.Icon
import mui.material.InputLabel
import mui.material.MenuItem
import mui.material.Select
import mui.material.Size
import mui.material.Stack
import mui.material.StackDirection
import mui.material.SvgIconSize
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
import org.aarbizu.baseballDatabankFrontend.getPaginationControls
import org.aarbizu.baseballDatabankFrontend.getSortedNames
import org.aarbizu.baseballDatabankFrontend.md
import org.aarbizu.baseballDatabankFrontend.queryPlayerNameLength
import org.aarbizu.baseballDatabankFrontend.scope
import org.aarbizu.baseballDatabankFrontend.xs
import react.ChildrenBuilder
import react.ReactNode
import react.VFC
import react.router.useLocation
import react.useState

fun ChildrenBuilder.getUpArrowIcon() {
    Icon {
        sx {
            fontSize = 1.05.em
            paddingLeft = 0.5.em
        }
        ArrowUpward { fontSize = SvgIconSize.inherit }
    }
}

fun ChildrenBuilder.getDownArrowIcon() {
    Icon {
        sx {
            fontSize = 1.05.em
            paddingLeft = 0.5.em
        }
        ArrowDownward { fontSize = SvgIconSize.inherit }
    }
}

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
                        """
                            .trimIndent()
                    }
                }
            }

            BasicPlayerList {
                playerList = players
                listType = "names-by-given-length"
                paginationControls = getPaginationControls(10, 0)
            }
        }
    }
}

val TopNNameLengths = VFC {
    var players by useState(emptyList<SimplePlayerRecord>())
    var listTypeProp by useState("default")
    var topNParam by useState("10")
    val pagination = getPaginationControls(10, 0)

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
                    sx {
                        marginLeft = Auto.auto
                        paddingTop = 1.em
                    }
                    direction = responsive(StackDirection.column)

                    FormControl {
                        fullWidth = false
                        InputLabel {
                            id = "top-name-select-label"
                            +"Player Count"
                        }
                        Select {
                            labelId = "top-name-select-label"
                            id = "top-name-select"
                            value = topNParam.unsafeCast<Nothing?>()
                            label = ReactNode("Player Count")
                            onChange = { event, _ -> topNParam = event.target.value }
                            listOf("10", "25", "50", "100").forEach {
                                MenuItem {
                                    value = it
                                    +it
                                }
                            }
                        }
                    }

                    Stack {
                        sx {
                            marginLeft = Auto.auto
                            paddingTop = 1.em
                        }
                        direction = responsive(StackDirection.row)
                        ButtonGroup {
                            variant = ButtonGroupVariant.outlined
                            color = ButtonGroupColor.secondary
                            Button {
                                size = Size.small
                                onClick = {
                                    listTypeProp = "last"
                                    scope.launch {
                                        players =
                                            getSortedNames(
                                                NamesSortedByLengthParam(
                                                    type = "last",
                                                    descending = "true",
                                                    topN = topNParam,
                                                ),
                                            )
                                    }
                                    pagination.setCurrentPg(0)
                                }
                                +"Last name "
                                getDownArrowIcon()
                            }
                            Button {
                                size = Size.small
                                onClick = {
                                    scope.launch {
                                        listTypeProp = "last"
                                        players =
                                            getSortedNames(
                                                NamesSortedByLengthParam(
                                                    type = "last",
                                                    descending = "false",
                                                    topN = topNParam,
                                                ),
                                            )
                                    }
                                    pagination.setCurrentPg(0)
                                }
                                +"last name "
                                getUpArrowIcon()
                            }
                            Button {
                                size = Size.small
                                onClick = {
                                    listTypeProp = "first"
                                    scope.launch {
                                        players =
                                            getSortedNames(
                                                NamesSortedByLengthParam(
                                                    type = "first",
                                                    descending = "true",
                                                    topN = topNParam,
                                                ),
                                            )
                                    }
                                    pagination.setCurrentPg(0)
                                }
                                +"first name "
                                getDownArrowIcon()
                            }
                            Button {
                                size = Size.small
                                onClick = {
                                    scope.launch {
                                        listTypeProp = "first"
                                        players =
                                            getSortedNames(
                                                NamesSortedByLengthParam(
                                                    type = "first",
                                                    descending = "false",
                                                    topN = topNParam,
                                                ),
                                            )
                                    }
                                    pagination.setCurrentPg(0)
                                }
                                +"first name "
                                getUpArrowIcon()
                            }
                        }
                    }
                    Stack {
                        sx {
                            marginLeft = Auto.auto
                            paddingTop = 1.em
                        }
                        ButtonGroup {
                            variant = ButtonGroupVariant.outlined
                            color = ButtonGroupColor.secondary
                            Button {
                                size = Size.small
                                onClick = {
                                    listTypeProp = "firstlast"
                                    scope.launch {
                                        players =
                                            getSortedNames(
                                                NamesSortedByLengthParam(
                                                    type = "firstlast",
                                                    descending = "true",
                                                    topN = topNParam,
                                                ),
                                            )
                                    }
                                    pagination.setCurrentPg(0)
                                }
                                +"given name "
                                getDownArrowIcon()
                            }
                            Button {
                                size = Size.small
                                onClick = {
                                    listTypeProp = "firstlast"
                                    scope.launch {
                                        players =
                                            getSortedNames(
                                                NamesSortedByLengthParam(
                                                    type = "firstlast",
                                                    descending = "false",
                                                    topN = topNParam,
                                                ),
                                            )
                                    }
                                    pagination.setCurrentPg(0)
                                }
                                +"given name "
                                getUpArrowIcon()
                            }
                            Button {
                                size = Size.small
                                onClick = {
                                    listTypeProp = "full"
                                    scope.launch {
                                        players =
                                            getSortedNames(
                                                NamesSortedByLengthParam(
                                                    type = "full",
                                                    descending = "true",
                                                    topN = topNParam,
                                                ),
                                            )
                                    }
                                    pagination.setCurrentPg(0)
                                }
                                +"full name "
                                getDownArrowIcon()
                            }
                            Button {
                                size = Size.small
                                onClick = {
                                    scope.launch {
                                        listTypeProp = "full"
                                        players =
                                            getSortedNames(
                                                NamesSortedByLengthParam(
                                                    type = "full",
                                                    descending = "false",
                                                    topN = topNParam,
                                                ),
                                            )
                                    }
                                    pagination.setCurrentPg(0)
                                }
                                +"full name "
                                getUpArrowIcon()
                            }
                        }
                    }
                }
            }

            BasicPlayerList {
                playerList = players
                listType = "names-by-given-length"
                paginationControls = pagination
            }
        }
    }
}
