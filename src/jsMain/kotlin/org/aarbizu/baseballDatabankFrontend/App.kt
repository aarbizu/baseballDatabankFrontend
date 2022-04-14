package org.aarbizu.baseballDatabankFrontend

import kotlinx.coroutines.MainScope
import react.FC
import react.Props
import react.create
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ul
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter
import react.router.dom.Link

val scope = MainScope()

val Home =
    FC<Props> {
        div {
            ul {
                li {
                    Link {
                        to = "/lastname"
                        +"Last Name Search"
                    }
                }
            }
        }
    }

val App =
    FC<Props> {
        BrowserRouter {
            Routes {
                Route {
                    path = "/"
                    element = Home.create()
                }

                Route {
                    path = "/lastname"
                    element = LastNameSearch.create()
                }
            }
        }
    }
