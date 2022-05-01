package org.aarbizu.baseballDatabankFrontend.routes

import mui.icons.material.Home
import mui.material.AppBar
import mui.material.AppBarPosition
import mui.material.Box
import mui.material.IconButton
import mui.material.Size
import mui.material.Toolbar
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.BasicMenu
import org.aarbizu.baseballDatabankFrontend.MinMaxValues
import org.aarbizu.baseballDatabankFrontend.myAppTheme
import react.FC
import react.Props
import react.router.Outlet
import react.router.useNavigate

external interface BaseballAppProps : Props {
    var minMax: MinMaxValues
}

val NavBar =
    FC<BaseballAppProps> {
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
