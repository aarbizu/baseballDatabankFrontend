package org.aarbizu.baseballDatabankFrontend.routes

import mui.icons.material.SportsBaseballOutlined
import mui.material.Icon
import mui.material.IconColor
import mui.material.IconSize
import react.VFC
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h3

val Home = VFC {
    h1 {
        Icon {
            color = IconColor.primary
            fontSize = IconSize.large
            SportsBaseballOutlined
        }
    }
    h3 {
        +"Baseball Databank"
    }
}
