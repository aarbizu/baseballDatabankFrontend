package org.aarbizu.baseballDatabankFrontend.routes

import mui.icons.material.Home
import mui.material.AppBar
import mui.material.AppBarPosition
import mui.material.Box
import mui.material.Button
import mui.material.IconButton
import mui.material.Size
import mui.material.Toolbar
import mui.material.Typography
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.myAppTheme
import react.VFC
import react.router.Outlet
import react.router.useHref

val NavBar = VFC {
    Box {
        AppBar {
            position = AppBarPosition.static
            Toolbar {
                IconButton {
                    size = Size.large
                    sx { color = myAppTheme.palette.secondary.main }
                    useHref(to = "/")
                    Home {} /* not the component, the mui icon */
                }
                Button {
                    Typography {
                        sx { color = myAppTheme.palette.secondary.main }
                        +"Players"
                    }
                }
            }
        }
    }
    Outlet {}
}
