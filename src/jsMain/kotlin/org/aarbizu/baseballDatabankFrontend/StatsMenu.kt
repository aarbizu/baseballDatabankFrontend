package org.aarbizu.baseballDatabankFrontend

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mui.material.Button
import mui.material.Menu
import mui.material.MenuItem
import mui.material.Typography
import mui.system.sx
import org.w3c.dom.Element
import react.FC
import react.Props
import react.router.NavigateOptions
import react.router.useNavigate
import react.useState

external interface StatsMenuProps : Props {
    var buttonLabel: String
    var offenseStatLabels: List<String>
}

val StatsMenu =
    FC<StatsMenuProps> { props ->
        var menuAnchorElem by useState<Element>()
        val navigate = useNavigate()

        Button {
            onClick = { menuAnchorElem = it.currentTarget }
            Typography {
                sx { color = myAppTheme.palette.secondary.main }
                +props.buttonLabel
            }
        }

        Menu {
            if (menuAnchorElem != null) {
                anchorEl = { menuAnchorElem as Element }
            }
            open = menuAnchorElem != null

            onClose = { menuAnchorElem = null }

            MenuItem {
                onClick = {
                    menuAnchorElem = null
                    navigate(
                        "/stats",
                        options =
                        object : NavigateOptions {
                            override var replace: Boolean? = false
                            override var state: Any? =
                                Json.encodeToString(props.offenseStatLabels)
                        }
                    )
                }
                +"Stats"
            }

            // TODO - need to create the /stats route now

            //            MenuItem {
            //                onClick = {
            //                    menuAnchorElem = null
            //                    navigate("/name")
            //                }
            //                +"By Name / Regex"
            //            }
        }
    }
