package org.aarbizu.baseballDatabankFrontend

import kotlinx.browser.window
import mui.material.Button
import mui.material.Menu
import mui.material.MenuItem
import mui.material.Typography
import mui.system.sx
import org.w3c.dom.Element
import react.FC
import react.Props
import react.router.useNavigate
import react.useState

external interface BasicMenuProps : Props {
    var buttonLabel: String
}

val BasicMenu = FC<BasicMenuProps> { props ->
    var menuAnchorElem by useState<Element>()
    val navigate = useNavigate()

    Button {
        onClick = {
            menuAnchorElem = it.currentTarget
        }
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

        MenuItem {
            onClick = {
                menuAnchorElem = null
                navigate("/lastnamelength")
            }
            +"Last Name Length"
        }

        MenuItem {
            onClick = {
                menuAnchorElem = null
                navigate("/name")
            }
            +"Name or Regex"
        }
    }
}