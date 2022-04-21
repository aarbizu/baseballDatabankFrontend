package org.aarbizu.baseballDatabankFrontend

import kotlinx.browser.document
import react.create
import react.dom.client.createRoot

fun main() {
    val container = document.getElementById("root") ?: error("Couldn't find container!")
    val root = createRoot(container)
    root.render(App.create())
}
