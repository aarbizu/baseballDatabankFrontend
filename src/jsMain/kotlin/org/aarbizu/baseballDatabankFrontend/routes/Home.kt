package org.aarbizu.baseballDatabankFrontend.routes

import csstype.ClassName
import react.VFC
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.i

val Home = VFC {
    div {
        className = ClassName("ui center aligned container")
        h1 {
            className = ClassName("ui icon header")
            i { className = ClassName("baseball ball icon") }
            div {
                className = ClassName("content")
                +"Baseball Databank"
            }
        }
    }
}
