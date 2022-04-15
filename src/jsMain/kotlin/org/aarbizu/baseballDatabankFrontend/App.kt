package org.aarbizu.baseballDatabankFrontend

import kotlinx.coroutines.MainScope
import react.FC
import react.Props
import react.create
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.style
import react.router.Outlet
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter
import react.router.dom.Link

val scope = MainScope()

val MainView =
    FC<Props> {
        div {
            h1 { +"Baseball Databank" }
            nav {
                style { type = "{{ borderBottom: solid 1px, paddingBottom: 1rem }}" }
                Link {
                    to = "/lastnamelength"
                    +"Last Name Search"
                }
                +" | "
                Link {
                    to = "/name"
                    +"Player Name Search"
                }
            }
            Outlet {}
        }
    }

val App =
    FC<Props> {
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
                }
            }
        }
    }
