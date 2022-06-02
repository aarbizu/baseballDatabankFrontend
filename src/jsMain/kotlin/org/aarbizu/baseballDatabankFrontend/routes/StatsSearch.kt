package org.aarbizu.baseballDatabankFrontend.routes

import csstype.TextAlign
import csstype.em
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mui.material.Box
import mui.material.Grid
import mui.material.GridDirection
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.PlayerCareerStatRecord
import react.VFC
import react.router.useLocation
import react.useState

val StatsSearch = VFC {
    val loc = useLocation()
    val playerStats by useState(emptyList<PlayerCareerStatRecord>())
    val statNames: List<String> = Json.decodeFromString(loc.state as String)

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

                Typography {
                    variant = TypographyVariant.body1
                    sx {
                        textAlign = TextAlign.right
                        paddingBottom = 0.1.em
                    }
                    +statNames.joinToString()
                }

                playerStats.forEach { it }
            }
        }
    }
}
