package org.aarbizu.baseballDatabankFrontend.routes

import kotlinx.serialization.json.JsonPrimitive
import kweb.ElementCreator
import kweb.a
import kweb.div
import kweb.i
import kweb.plugins.fomanticUI.fomantic
import kweb.state.KVar

const val TOP_LEVEL_MENU_LOCATION = "/q/begin"

val massiveButtonStyle = mapOf("class" to JsonPrimitive("ui massive orange right labeled icon button"))
val buttonStyle = mapOf("class" to JsonPrimitive("ui large orange right labeled icon button"))
val baseballGlyphStyle = mapOf("class" to JsonPrimitive("baseball ball icon"))
val subHeaderStyle = mapOf("class" to JsonPrimitive("sub header"))
val fieldButtonStyle = mapOf("class" to JsonPrimitive(buttonStyle["class"].toString() + " field"))
val homeIcon = mapOf("class" to JsonPrimitive("home icon"))

data class Crumb(val section: String, val url: String) {
    fun isNotEmpty() = section.isNotBlank() && url.isNotBlank()
    fun isEmpty() = !isNotEmpty()

    companion object EmptyCrumb {
        val empty = Crumb("", "")
    }
}

fun ElementCreator<*>.appendCrumb(newCrumb: Crumb, crumbs: MutableList<Crumb>) {
    if (newCrumb.isEmpty()) {
        crumbs.clear()
    }

    if (!crumbs.contains(newCrumb) && newCrumb.isNotEmpty()) {
        crumbs.add(newCrumb)
    }

    crumbs.forEach {
        i(fomantic.right.chevron.icon.divider)
        a(fomantic.section, href = it.url).text(it.section)
    }
}

fun ElementCreator<*>.debugParamsElement(parameters: Map<String, KVar<String>>) =
    if (debug) {
        div(fomantic.content).text(parameters.entries.mapIndexed { idx, entry ->
            "$idx: ${entry.key}=${entry.value.map { it }}"
        }.joinToString())
    } else {
        div()
    }
