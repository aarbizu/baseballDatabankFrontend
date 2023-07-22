package org.aarbizu.baseballDatabankFrontend

import kotlinx.serialization.Serializable
import mui.material.Menu
import mui.material.MenuItem
import react.FC
import react.router.useNavigate
import react.useState
import web.dom.Element

@Serializable data class StatNames(var hitting: List<String>, var pitching: List<String>)

val StatsMenu =
    FC<BasicMenuProps> { props ->
        val (menuAnchorElem, menuAnchorSetter) = useState<Element>()
        val navigate = useNavigate()

        addMenuButton(menuAnchorSetter, props)

        Menu {
            if (menuAnchorElem != null) {
                anchorEl = { menuAnchorElem }
            }
            open = menuAnchorElem != null

            onClose = { menuAnchorSetter(null) }

            MenuItem {
                onClick = {
                    menuAnchorSetter(null)
                    navigate("/stats")
                }
                +"Stats"
            }
        }
    }
