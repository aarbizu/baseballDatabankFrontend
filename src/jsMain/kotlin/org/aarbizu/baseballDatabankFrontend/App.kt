package org.aarbizu.baseballDatabankFrontend

import csstype.rgba
import js.core.jso
import kotlinx.coroutines.MainScope
import mui.material.styles.ThemeProvider
import mui.material.styles.createTheme
import org.aarbizu.baseballDatabankFrontend.routes.Home
import org.aarbizu.baseballDatabankFrontend.routes.NameLengthSearch
import org.aarbizu.baseballDatabankFrontend.routes.NavBar
import org.aarbizu.baseballDatabankFrontend.routes.NoMatch
import org.aarbizu.baseballDatabankFrontend.routes.PlayerNameSearch
import org.aarbizu.baseballDatabankFrontend.routes.StatsSearch
import org.aarbizu.baseballDatabankFrontend.routes.TopNNameLengths
import org.reduxkotlin.createThreadSafeStore
import react.VFC
import react.create
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter

const val bbrefUri = "https://www.baseball-reference.com"
const val bbrefSuffix = ".shtml"

val scope = MainScope()

data class BBStore(
    var minMaxNameValues: String,
    var pitchingStatNames: List<String>,
    var hittingStateNames: List<String>,
)

data class AddMinMaxValues(val text: String)
data class AddPitchingStatNames(val text: String)
data class AddHittingStatNames(val text: String)

typealias Reducer<State> = (state: State, action: Any) -> State
val nonAlphaRex = """[\[\]"]""".toRegex()
val reducer: Reducer<BBStore> = { state, action ->
    when (action) {
        is AddMinMaxValues -> state.copy(minMaxNameValues = JSON.parse(action.text))
        is AddPitchingStatNames -> state.copy(pitchingStatNames = action.text.replace(nonAlphaRex, "").split(","))
        is AddHittingStatNames -> state.copy(hittingStateNames = action.text.replace(nonAlphaRex, "").split(","))
        else -> state
    }
}

val store = createThreadSafeStore(reducer, BBStore("", emptyList(), emptyList()))

val myAppTheme =
    createTheme(
        jso {
            palette = jso {
                primary = jso {
                    main = "rgb(0,0,0)"
                    darker = "rgb(62,62,62)"
                }

                secondary = jso {
                    main = "rgb(249,82,0)"
                    darker = "rgb(166,76,31)"
                }
                text = jso {
                    primary = rgba(0, 0, 0, 0.87)
                    secondary = rgba(145, 145, 145, 0.8)
                    disabled = rgba(0, 0, 0, 0.6)
                }
            }
        },
    )

val App = VFC {
    ThemeProvider {
        theme = myAppTheme
        BrowserRouter {
            Routes {
                Route {
                    path = "/"
                    element = NavBar.create()

                    Route {
                        index = true
                        element = Home.create()
                    }

                    Route {
                        path = "namelength"
                        element = NameLengthSearch.create()
                    }

                    Route {
                        path = "topNNameLengths"
                        element = TopNNameLengths.create()
                    }

                    Route {
                        path = "name"
                        element = PlayerNameSearch.create()
                    }

                    Route {
                        path = "stats"
                        element = StatsSearch.create()
                    }

                    Route {
                        path = "*"
                        element = NoMatch.create()
                    }
                }
            }
        }
    }
}
