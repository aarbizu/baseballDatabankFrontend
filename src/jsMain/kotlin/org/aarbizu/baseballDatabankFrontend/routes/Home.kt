package org.aarbizu.baseballDatabankFrontend.routes

import csstype.AlignItems
import csstype.Auto
import csstype.FontWeight
import csstype.rem
import mui.icons.material.SportsBaseballOutlined
import mui.material.Icon
import mui.material.Stack
import mui.material.Typography
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.myAppTheme
import react.VFC

val Home = VFC {
    Stack {
        sx {
            alignItems = AlignItems.center
            margin = Auto.auto
        }
        Icon {
            sx {
                fontSize = 10.rem
                marginRight = 0.7.rem
            }
            SportsBaseballOutlined {
                sx {
                    fontSize = 10.rem
                    color = myAppTheme.palette.secondary.main
                }
            }
        }
        Typography {
            sx {
                fontSize = 2.rem
                fontWeight = FontWeight.bold
            }
            +"Baseball Databank"
        }
    }
}
