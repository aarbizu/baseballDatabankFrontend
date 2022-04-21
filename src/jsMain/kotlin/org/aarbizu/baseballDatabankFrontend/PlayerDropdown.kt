package org.aarbizu.baseballDatabankFrontend

import react.VFC
import react.dom.html.ReactHTML.div
import react.router.dom.NavLink

var PlayerDropdown = VFC {
    div {
        NavLink {
            to = "/name"
            +"Search by Name or Regex"
        }
        NavLink {
            to = "/lastnamelength"
            +"Search By Name Length"
        }
    }
}
