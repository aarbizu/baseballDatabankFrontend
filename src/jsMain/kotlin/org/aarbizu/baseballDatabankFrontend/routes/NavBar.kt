package org.aarbizu.baseballDatabankFrontend.routes

import kotlinx.coroutines.launch
import mui.icons.material.Home
import mui.material.AppBar
import mui.material.AppBarPosition
import mui.material.Box
import mui.material.IconButton
import mui.material.Size
import mui.material.Toolbar
import mui.system.sx
import org.aarbizu.baseballDatabankFrontend.AddHittingStatNames
import org.aarbizu.baseballDatabankFrontend.AddMinMaxValues
import org.aarbizu.baseballDatabankFrontend.AddPitchingStatNames
import org.aarbizu.baseballDatabankFrontend.BasicMenu
import org.aarbizu.baseballDatabankFrontend.ModernMLBDivisions
import org.aarbizu.baseballDatabankFrontend.SeasonStandingsMenu
import org.aarbizu.baseballDatabankFrontend.StatsMenu
import org.aarbizu.baseballDatabankFrontend.getMinMaxNameLengths
import org.aarbizu.baseballDatabankFrontend.getModernMLBDivisions
import org.aarbizu.baseballDatabankFrontend.getOffenseStatNames
import org.aarbizu.baseballDatabankFrontend.getPitchingStatNames
import org.aarbizu.baseballDatabankFrontend.myAppTheme
import org.aarbizu.baseballDatabankFrontend.scope
import org.aarbizu.baseballDatabankFrontend.store
import react.VFC
import react.router.Outlet
import react.router.useNavigate
import react.useEffectOnce

val NavBar = VFC {

    useEffectOnce {
        scope.launch {
            store.dispatch(AddMinMaxValues(getMinMaxNameLengths()))
            store.dispatch(AddHittingStatNames(getOffenseStatNames()))
            store.dispatch(AddPitchingStatNames(getPitchingStatNames()))
            store.dispatch(ModernMLBDivisions(getModernMLBDivisions()))
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
                    Home {} // not the component, the MUI icon
                }

                BasicMenu {
                    buttonLabel = "Players"
                }

                StatsMenu {
                    buttonLabel = "Stats"
                }

                SeasonStandingsMenu {
                    buttonLabel = "Standings"
                }
            }
        }
    }
    Outlet {}
}
