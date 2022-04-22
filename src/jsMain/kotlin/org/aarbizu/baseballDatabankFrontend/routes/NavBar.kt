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
import org.aarbizu.baseballDatabankFrontend.BasicMenu
import org.aarbizu.baseballDatabankFrontend.myAppTheme
import react.VFC
import react.router.Outlet
import react.router.useHref
import react.router.useNavigate

val NavBar = VFC {
    val navigate = useNavigate()
    Box {
        AppBar {
            position = AppBarPosition.static
            Toolbar {
                IconButton {
                    size = Size.large
                    sx { color = myAppTheme.palette.secondary.main }
                    onClick = { navigate("/") }
                    Home {} /* not the component, the mui icon */
                }
                BasicMenu { buttonLabel = "Players" }
            }
        }
    }
    Outlet {}
}
