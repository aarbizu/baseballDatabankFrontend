package org.aarbizu.baseballDatabankFrontend

import io.ktor.http.Parameters
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kweb.Element
import kweb.ElementCreator
import kweb.InputElement
import kweb.InputType
import kweb.a
import kweb.button
import kweb.div
import kweb.form
import kweb.h1
import kweb.i
import kweb.id
import kweb.input
import kweb.label
import kweb.new
import kweb.plugins.fomanticUI.fomantic
import kweb.routing.RouteReceiver
import kweb.state.KVar
import org.aarbizu.baseballDatabankFrontend.config.PaginatedRecords

private val massiveButtonStyle = mapOf("class" to "ui massive orange right labeled icon button")
private val buttonStyle = mapOf("class" to "ui large orange right labeled icon button")
private val baseballGlyphStyle = mapOf("class" to "baseball ball icon")
private val subHeaderStyle = mapOf("class" to "sub header")
private val homeIcon = mapOf("class" to "home icon")
private val fieldButtonStyle = mapOf("class" to buttonStyle["class"] + " field")

private const val playerNameLengthQuery = "player-name-by-len"
private const val playerNameSearchQuery = "player-name"

private val queryEngine = QueryEngine()
private val crumbs = mutableListOf<Crumb>()

data class Crumb(val section: String, val url: String) {
    fun isNotEmpty() = section.isNotBlank() && url.isNotBlank()
    fun isEmpty() = !isNotEmpty()
    companion object EmptyCrumb {
        val empty = Crumb("", "")
    }
}

private fun ElementCreator<*>.renderNavMenu(newCrumb: Crumb) {
    div(fomantic.ui.attached.inverted.segment).new {
        div(fomantic.ui.inverted.breadcrumb).new {
            a(fomantic.section, href = "/").new {
                div(fomantic.ui.item).new {
                    i(homeIcon)
                }
            }
            appendSection(newCrumb)
        }
    }
}

private fun ElementCreator<*>.appendSection(newCrumb: Crumb) {
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

fun RouteReceiver.getRoutePaths(parameters: Parameters) {
    path("/") {
        renderNavMenu(Crumb.empty)
        div(fomantic.ui.center.aligned.container).new {
            div(fomantic.ui.hidden.divider)
            div(fomantic.content).new {
                h1(fomantic.ui.icon.header).new {
                    i(baseballGlyphStyle)
                    div(fomantic.content).text("Baseball Databank").new {
                        div(subHeaderStyle).text("Historical baseball database derived from: ").new {
                            a(href = "https://github.com/chadwickbureau/baseballdatabank")
                                .text("The Chadwick Baseball Databank").tag
                        }
                    }
                }
                div(fomantic.content).new {
                    a(href = "http://chadwick-bureau.com/")
                        .text("Visit the Chadwick Baseball Bureau")
                }
                div(fomantic.ui.divider)
                button(massiveButtonStyle).text("Get Started").on.click {
                    browser.url.value = "/q/begin"
                }.new {
                    i(fomantic.ui.icon.right.arrow)
                }
            }
        }
    }
    path("/q/begin") {
        renderNavMenu(Crumb("Begin", "/q/begin"))
        div(fomantic.ui.hidden.divider)
        div(fomantic.ui.container).new {
            div(fomantic.ui.two.item.menu).new {
                a(fomantic.ui.item).text("Player Name By Length").on.click {
                    browser.url.value = "/q/$playerNameLengthQuery/"
                }
                a(fomantic.ui.item).text("Player Name Search").on.click {
                    browser.url.value = "/q/$playerNameSearchQuery/"
                }
            }
        }
    }
    path("/q/$playerNameLengthQuery/{lengthParam}") {
        renderNavMenu(Crumb("Name Length", "/q/$playerNameLengthQuery/${parameters[playerNameLengthQuery]}"))
        div(fomantic.ui.hidden.divider)
        div(fomantic.ui.container).new {
            generatePlayerNameLengthForm(queryEngine)
            div(fomantic.content).text(parameters.entries().mapIndexed {
                idx, entry -> "$idx: ${entry.key}=${entry.value.map { it }}"
            }.joinToString())
        }
    }
    path("/q/$playerNameSearchQuery/{nameParam}") {
        renderNavMenu(Crumb("Last Name", "/q/$playerNameSearchQuery/${parameters[playerNameSearchQuery]}"))
        div(fomantic.ui.hidden.divider)
        div(fomantic.ui.container).new {
            generatePlayerNameSearchForm(queryEngine)
            div(fomantic.content).text(parameters.entries().mapIndexed {
                    idx, entry -> "$idx: ${entry.key}=${entry.value.map { it }}"
            }.joinToString())
        }
    }
}

private fun ElementCreator<*>.generatePlayerNameSearchForm(queries: QueryEngine) {
    val output = KVar("")
    var nameFragmentInput: InputElement? = null

    form(fomantic.ui.form).new {
        div(fomantic.fields).new {
            div(fomantic.six.wide.field).new {
                label("Last Name")
                nameFragmentInput = input(
                    type = InputType.text,
                    name = "last-name",
                    initialValue = "",
                    size = 32,
                    placeholder = """ Surname, e.g."Bonds" """
                )
            }
            button(fieldButtonStyle).text("Search").on.click {
                GlobalScope.launch {
                    getInputAndRenderResult(listOf(nameFragmentInput!!),
                        browser.doc.getElementById("names")) {
                        inputs -> queries.playerNameSearch("%${inputs[0].toLowerCase()}%")
                    }
                }
            }.new {
                i(baseballGlyphStyle)
            }
        }
    }
    div(fomantic.ui.hidden.divider)
    div(fomantic.content.id("names")).text(output)
}

private fun ElementCreator<*>.generatePlayerNameLengthForm(queries: QueryEngine) {
    val output = KVar("")
    var nameLengthInput: InputElement? = null

    form(fomantic.ui.form).new {
        div(fomantic.fields).new {
            div(fomantic.three.wide.field).new {
                label("Name Length")
                nameLengthInput = input(
                    type = InputType.text,
                    name = "name-length",
                    initialValue = "",
                    size = 2,
                    placeholder = "Name Length"
                )
            }
            button(fieldButtonStyle).text("Search").on.click {
                GlobalScope.launch {
                    getInputAndRenderResult(listOf(nameLengthInput!!),
                        browser.doc.getElementById("output")) {
                        inputs -> queries.playerNamesByLength(inputs[0])
                    }
                }
            }.new {
                i(baseballGlyphStyle)
            }
        }
    }
    div(fomantic.ui.hidden.divider)
    div(fomantic.content.id("output")).text(output)
}

private suspend fun getInputAndRenderResult(
    inputs: List<InputElement>,
    outputElement: Element,
    query: (inputs: List<String>) -> List<TableRecord>
) {
    val params = mutableListOf<String>()
    inputs.forEach {
        params.add(it.getValue().await())
    }
    getQueryResult(params, query, outputElement)
}

private fun getQueryResult(
    params: List<String>,
    query: (inputs: List<String>) -> List<TableRecord>,
    outputElement: Element
) {
    PaginatedRecords(query(params), outputElement).renderTable()
}
