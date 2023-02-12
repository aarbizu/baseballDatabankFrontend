package org.aarbizu.baseballDatabankFrontend

import mui.material.Button
import mui.material.Menu
import mui.material.MenuItem
import mui.material.Typography
import mui.system.sx
import react.ChildrenBuilder
import react.FC
import react.Props
import react.StateSetter
import react.router.useNavigate
import react.useState
import web.dom.Element

external interface BasicMenuProps : Props {
    var buttonLabel: String
}

fun ChildrenBuilder.addMenuButton(setter: StateSetter<Element?>, props: BasicMenuProps) {
    Button {
        onClick = { setter(it.currentTarget) }
        Typography {
            sx { color = myAppTheme.palette.secondary.main }
            +props.buttonLabel
        }
    }
}

// TODO pull more into props to make it reusable
val BasicMenu =
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
                    navigate("/namelength")
                }
                +"By Name Length"
            }

            MenuItem {
                onClick = {
                    menuAnchorSetter(null)
                    navigate("/topNNameLengths")
                }
                +"TopN Name Lengths"
            }

            MenuItem {
                onClick = {
                    menuAnchorSetter(null)
                    navigate("/name")
                }
                +"By Name / Regex"
            }
        }
    }
