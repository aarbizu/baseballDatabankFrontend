package org.aarbizu.baseballDatabankFrontend

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

external interface BasicMenuProps : Props {
    var buttonLabel: String
    var minMax: String /* json for MinMaxValue */
}

// TODO pull more into props to make it reusable
val BasicMenu =
    FC<BasicMenuProps> { props ->
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
                        "/namelength",
                        options =
                        object : NavigateOptions {
                            override var replace: Boolean? = false
                            override var state: Any? = props.minMax
                        }
                    )
                }
                +"By Name Length"
            }

            MenuItem {
                onClick = {
                    menuAnchorElem = null
                    navigate("/name")
                }
                +"By Name / Regex"
            }
        }
    }
