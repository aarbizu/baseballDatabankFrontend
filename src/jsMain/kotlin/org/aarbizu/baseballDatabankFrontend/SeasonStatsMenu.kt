package org.aarbizu.baseballDatabankFrontend

import mui.material.Menu
import mui.material.MenuItem
import react.FC
import react.router.useNavigate
import react.useState
import web.dom.Element

val SeasonStandingsMenu =
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
                    navigate("/modern-standings")
                }
                +"1901 - Present"
            }
        }
    }
