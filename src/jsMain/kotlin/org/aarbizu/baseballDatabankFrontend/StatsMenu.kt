package org.aarbizu.baseballDatabankFrontend

import history.LocationState
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mui.material.Menu
import mui.material.MenuItem
import web.dom.Element
import react.FC
import react.router.useNavigate
import react.useState

external interface StatsMenuProps : BasicMenuProps {
    var offenseStatLabels: List<String>
    var pitchingStatLabels: List<String>
}

@Serializable data class StatNames(var hitting: List<String>, var pitching: List<String>)

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
                //Json.encodeToString( StatNames(props.offenseStatLabels, props.pitchingStatLabels), )
                onClick = {
                    menuAnchorSetter(null)
                    navigate("/stats")
                }
                +"Stats"
            }
        }
    }
