package org.aarbizu.baseballDatabankFrontend

import mui.material.Button
import mui.material.Menu
import mui.material.MenuItem
import mui.material.Typography
import mui.system.sx
import org.w3c.dom.Element
import react.ChildrenBuilder
import react.FC
import react.Props
import react.StateSetter
import react.router.NavigateOptions
import react.router.useNavigate
import react.useState

external interface BasicMenuProps : Props {
    var buttonLabel: String
}

external interface PlayerMenuProps : BasicMenuProps {
    var minMax: String /* json for MinMaxValue */
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
    FC<PlayerMenuProps> { props ->
        var (menuAnchorElem, menuAnchorSetter) = useState<Element>()
        val navigate = useNavigate()

        addMenuButton(menuAnchorSetter, props)

        Menu {
            if (menuAnchorElem != null) {
                anchorEl = { menuAnchorElem as Element }
            }
            open = menuAnchorElem != null

            onClose = { menuAnchorSetter(null) }

            MenuItem {
                onClick = {
                    menuAnchorSetter(null)
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
