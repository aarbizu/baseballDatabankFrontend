package org.aarbizu.baseballDatabankFrontend

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mui.material.Menu
import mui.material.MenuItem
import org.w3c.dom.Element
import react.FC
import react.router.NavigateOptions
import react.router.useNavigate
import react.useState

external interface StatsMenuProps : BasicMenuProps {
    var offenseStatLabels: List<String>
}

val StatsMenu =
    FC<StatsMenuProps> { props ->
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
            
        }
    }
