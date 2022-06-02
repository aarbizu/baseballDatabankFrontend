package org.aarbizu.baseballDatabankFrontend.routes

import csstype.FontWeight
import csstype.JustifyContent
import csstype.TextAlign
import csstype.rem
import mui.icons.material.SportsBaseballOutlined
import mui.material.Container
import mui.material.Grid
import mui.material.GridDirection
import mui.material.Icon
import mui.material.IconColor
import mui.material.SvgIconSize
import mui.material.Typography
import mui.system.responsive
import mui.system.sx
import react.VFC

val Home = VFC {
    Container {
        maxWidth = "sm"
        sx { paddingBottom = 0.5.rem }

        Grid {
            container = true
            direction = responsive(GridDirection.column)
            spacing = responsive(2)
            sx { justifyContent = JustifyContent.center }

            Grid {
                item = true
                sx { textAlign = TextAlign.center }
                Icon {
                    sx { fontSize = 10.rem }
                    color = IconColor.secondary
                    SportsBaseballOutlined { fontSize = SvgIconSize.inherit }
                }
            }
            Grid {
                item = true
                sx { textAlign = TextAlign.center }
                Typography {
                    sx {
                        fontSize = 2.rem
                        fontWeight = FontWeight.bold
                    }
                    +"fungo|stats"
                }
            }
        }
    }
}
