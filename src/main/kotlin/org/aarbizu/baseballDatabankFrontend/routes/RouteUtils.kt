package org.aarbizu.baseballDatabankFrontend.routes

import kweb.ElementCreator
import kweb.a
import kweb.div
import kweb.i
import kweb.new
import kweb.plugins.fomanticUI.fomantic

val massiveButtonStyle = mapOf("class" to "ui massive orange right labeled icon button")
val buttonStyle = mapOf("class" to "ui large orange right labeled icon button")
val baseballGlyphStyle = mapOf("class" to "baseball ball icon")
val subHeaderStyle = mapOf("class" to "sub header")
val fieldButtonStyle = mapOf("class" to buttonStyle["class"] + " field")

const val playerNameLength = "player-name-by-len"
const val pPlayerNameLength = "name-length"

const val playerLastNameSearchQuery = "player-name"
const val pPlayerLastNameParam = "last-name"

const val playerNameRegex = "player-name-regex"
const val pPlayerRegexParam = "regex"
const val pPlayerRegexFnameParam = "regex-first-name"
const val pPlayerRegexLnameParam = "regex-last-name"
const val pPlayerRegexCaseSensitive = "regex-case-sensitive"

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

fun ElementCreator<*>.renderNavMenu(newCrumb: Crumb, crumbs: MutableList<Crumb>) {
            div(fomantic.ui.attached.inverted.segment).new {
                div(fomantic.ui.inverted.breadcrumb).new {
                    a(fomantic.section, href = "/").new {
                        div(fomantic.ui.item).new {
                            i(homeIcon)
                        }
                    }
                    appendCrumb(newCrumb, crumbs)
                }
            }
}
