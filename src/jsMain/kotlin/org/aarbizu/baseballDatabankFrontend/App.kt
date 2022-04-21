package org.aarbizu.baseballDatabankFrontend

import kotlinx.coroutines.MainScope
import org.aarbizu.baseballDatabankFrontend.routes.Home
import org.aarbizu.baseballDatabankFrontend.routes.LastNameLengthSearch
import org.aarbizu.baseballDatabankFrontend.routes.NavBar
import org.aarbizu.baseballDatabankFrontend.routes.NoMatch
import org.aarbizu.baseballDatabankFrontend.routes.PlayerNameSearch
import react.VFC
import react.create
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter

val scope = MainScope()

val App = VFC {
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
                    path = "lastnamelength"
                    element = LastNameLengthSearch.create()
                }

                Route {
                    path = "name"
                    element = PlayerNameSearch.create()
                }
            }

            Route {
                path = "*"
                element = NoMatch.create()
            }
        }
    }
}
