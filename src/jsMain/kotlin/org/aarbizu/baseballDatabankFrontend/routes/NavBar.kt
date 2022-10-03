package org.aarbizu.baseballDatabankFrontend.routes

import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
import org.aarbizu.baseballDatabankFrontend.OffenseStats
import org.aarbizu.baseballDatabankFrontend.PitchingStats
import org.aarbizu.baseballDatabankFrontend.StatsMenu
import org.aarbizu.baseballDatabankFrontend.getMinMaxNameLengths
import org.aarbizu.baseballDatabankFrontend.getOffenseStatNames
import org.aarbizu.baseballDatabankFrontend.getPitchingStatNames
import org.aarbizu.baseballDatabankFrontend.myAppTheme
import org.aarbizu.baseballDatabankFrontend.scope
import react.VFC
import react.router.Outlet
import react.router.useNavigate
import react.useEffectOnce
import react.useState

val INIT_MIN_MAX = MinMaxValues("0", "0", "0", "0", "0", "0", "0", "0")

val NavBar = VFC {
    var minMaxValues by useState(INIT_MIN_MAX)
    var offenseStatNames by useState(OffenseStats(emptyList()))
    var pitchingStatNNames by useState(PitchingStats(emptyList()))

    useEffectOnce {
        scope.launch {
            minMaxValues = getMinMaxNameLengths()
            offenseStatNames = getOffenseStatNames()
            pitchingStatNNames = getPitchingStatNames()
        }
    }

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

                BasicMenu {
                    buttonLabel = "Players"
                    minMax = Json.encodeToString(minMaxValues)
                }

                StatsMenu {
                    buttonLabel = "Stats"
                    offenseStatLabels = offenseStatNames.statNames
                    pitchingStatLabels = pitchingStatNNames.statNames
                }
            }
        }
    }
    Outlet {}
}
