package org.aarbizu.baseballDatabankFrontend.routes

import kotlinx.coroutines.launch
import mui.material.Box
import mui.material.Button
import mui.material.FormControl
import mui.material.FormControlVariant
import mui.material.Grid
import mui.material.InputLabel
import mui.material.MenuItem
import mui.material.Select
import mui.material.Size
import mui.material.Stack
import mui.material.StackDirection.Companion.row
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.SeasonDailyStandingsParam
import org.aarbizu.baseballDatabankFrontend.getModernStandings
import org.aarbizu.baseballDatabankFrontend.scope
import org.aarbizu.baseballDatabankFrontend.store
import react.ReactNode
import react.VFC
import react.dom.html.ReactHTML.img
import react.useState
import web.cssom.TextAlign
import web.cssom.em
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun keys(json: dynamic) = js("Object").keys(json).unsafeCast<Array<String>>()

@OptIn(ExperimentalEncodingApi::class)
val ModernSeasonStandings = VFC {
    val modernMLBDivisions = store.getState().modernMLBDivisions.asDynamic()
    var selection by useState("")
    var image by useState("")

    Box {
        sx { padding = 1.em }

        Typography {
            variant = TypographyVariant.body1
            sx {
                textAlign = TextAlign.center
                paddingBottom = 0.1.em
            }
            +"Modern Era Daily Standings"
        }

        Stack {
            direction = responsive(row)
            FormControl {
                sx { margin = 1.em }
                variant = FormControlVariant.standard
                size = Size.small
                InputLabel {
                    id = "daily-standings"
                    +"Daily Standings"
                }
                Select {
                    labelId = "standings-select"
                    value = selection.unsafeCast<Nothing?>()
                    label = ReactNode("Season/Division")
                    onChange = { event, _ -> selection = event.target.value }
                    for (k in keys(modernMLBDivisions)) {
                        val divs = "${modernMLBDivisions[k]}"
                        divs.split(",").forEach {
                            MenuItem {
                                value = "$k,${it.trim()}"
                                +"$k - ${it.trim()}"
                            }
                        }
                    }
                }
                Button {
                    size = Size.small
                    onClick = {
                        if (
                            selection.isNotBlank() ||
                            selection.isNotEmpty()
                        ) {
                            scope.launch {
                                val (year, div) = selection.split(",")
                                image = getModernStandings(SeasonDailyStandingsParam(year, div))
                            }
                        }
                    }
                    +"Get Standings"
                }
            }

            Grid {
                container = true
                if (image.isNotEmpty()) {
                    val imgB64 = Base64.encode(image.encodeToByteArray())
                    img {
                        src = "data:image/svg+xml;base64,$imgB64"
                    }
                }
            }
        }
    }
}
