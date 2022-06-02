package org.aarbizu.baseballDatabankFrontend

import csstype.rgba
import kotlinx.coroutines.MainScope
import kotlinx.js.jso
import mui.material.styles.ThemeProvider
import mui.material.styles.createTheme
import org.aarbizu.baseballDatabankFrontend.routes.Home
import org.aarbizu.baseballDatabankFrontend.routes.NameLengthSearch
import org.aarbizu.baseballDatabankFrontend.routes.NavBar
import org.aarbizu.baseballDatabankFrontend.routes.NoMatch
import org.aarbizu.baseballDatabankFrontend.routes.PlayerNameSearch
import org.aarbizu.baseballDatabankFrontend.routes.StatsSearch
import react.VFC
import react.create
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter

val scope = MainScope()

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
        }
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
