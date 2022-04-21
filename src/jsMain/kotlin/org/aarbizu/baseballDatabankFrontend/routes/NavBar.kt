package org.aarbizu.baseballDatabankFrontend.routes

import csstype.ClassName
import react.VFC
import react.create
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.i
import react.dom.html.ReactHTML.nav
import react.router.Outlet
import react.router.dom.NavLink

val NavBar = VFC {
    div {
        nav {
            className = ClassName("ui attached inverted segment")

            div {
                className = ClassName("ui inverted breadcrumb")

                NavLink {
                    className = ClassName("section")
                    to = "/"
                    div {
                        className = ClassName("ui item")
                        i { className = ClassName("home icon") }
                    }
                }

                div {
                    className = ClassName("divider")
                    +"|"
                }

                NavLink {
                    className = ClassName("section")
                    to = "/lastnamelength"
                    +"Name length"
                }

                div {
                    className = ClassName("divider")
                    +"|"
                }

                NavLink {
                    className = ClassName("section")
                    //                state = """
                    //                    "from": "/"
                    //                """.trimIndent()
                    to = "/name"
                    +"Name or Regex"
                }
            }
        }

        Outlet {}
    }
}
