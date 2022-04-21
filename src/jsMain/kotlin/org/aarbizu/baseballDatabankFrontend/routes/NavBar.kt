package org.aarbizu.baseballDatabankFrontend.routes

import csstype.ClassName
import mui.icons.material.Home
import mui.material.Icon
import mui.material.IconColor
import mui.material.IconSize
import org.aarbizu.baseballDatabankFrontend.PlayerDropdown
import react.VFC
import react.create
import react.dom.html.ReactHTML.div
import react.router.Outlet
import react.router.dom.NavLink

val NavBar = VFC {
    div {

        NavLink {
            to = "/"
            div {
                Icon {
                    color = IconColor.primary
                    fontSize = IconSize.large
                    Home
                }
            }
        }

        div {
            +"Players"
            PlayerDropdown.create()
        }

        Outlet {}
    }
}
