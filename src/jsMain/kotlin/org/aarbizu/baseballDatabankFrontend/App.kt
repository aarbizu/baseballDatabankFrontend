package org.aarbizu.baseballDatabankFrontend

import kotlinx.coroutines.MainScope
import react.VFC
import react.create
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.p
import react.router.Outlet
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter
import react.router.dom.Link

val scope = MainScope()

val MainView = VFC {
    div {
        nav {
            Link {
                to = "/lastnamelength"
                +"Name length"
            }
            +" | "
            Link {
                //                state = """
                //                    "from": "/"
                //                """.trimIndent()
                to = "/name"
                +"Name or Regex"
            }
        }
        Outlet {}
    }
}

val NoMatch = VFC {
    div {
        h3 {
            +"Nothing to see here"
        }
        p {
            Link {
                to = "/"
                +"Home"
            }
        }
    }
}

val App = VFC {
    BrowserRouter {
        Routes {
            Route {
                path = "/"
                element = MainView.create()

                Route {
                    path = "/lastnamelength"
                    element = LastNameLengthSearch.create()
                }

                Route {
                    path = "/name"
                    element = PlayerNameSearch.create()
                }

                Route {
                    path = "*"
                    element = NoMatch.create()
                }
            }
        }
    }
}
