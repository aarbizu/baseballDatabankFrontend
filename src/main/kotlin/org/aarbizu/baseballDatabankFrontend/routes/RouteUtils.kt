package org.aarbizu.baseballDatabankFrontend.routes

import io.ktor.http.Parameters
import kweb.ElementCreator
import kweb.a
import kweb.div
import kweb.i
import kweb.plugins.fomanticUI.fomantic

const val TOP_LEVEL_MENU_LOCATION = "/q/begin"

val massiveButtonStyle = mapOf("class" to "ui massive orange right labeled icon button")
val buttonStyle = mapOf("class" to "ui large orange right labeled icon button")
val baseballGlyphStyle = mapOf("class" to "baseball ball icon")
val subHeaderStyle = mapOf("class" to "sub header")
val fieldButtonStyle = mapOf("class" to buttonStyle["class"] + " field")
val homeIcon = mapOf("class" to "home icon")

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

fun ElementCreator<*>.debugParamsElement(parameters: Parameters) =
    if (debug) {
        div(fomantic.content).text(parameters.entries().mapIndexed { idx, entry ->
            "$idx: ${entry.key}=${entry.value.map { it }}"
        }.joinToString())
    } else {
        div()
    }
