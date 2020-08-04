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
import org.slf4j.LoggerFactory

private val massiveButtonStyle = mapOf("class" to "ui massive orange right labeled icon button")
private val buttonStyle = mapOf("class" to "ui large orange right labeled icon button")
private val baseballGlyphStyle = mapOf("class" to "baseball ball icon")
private val subHeaderStyle = mapOf("class" to "sub header")
private val homeIcon = mapOf("class" to "home icon")
private val fieldButtonStyle = mapOf("class" to buttonStyle["class"] + " field")

private const val playerNameLength = "player-name-by-len"
private const val pPlayerNameLength = "name-length"

private const val playerLastNameSearchQuery = "player-name"
private const val pPlayerLastNameParam = "last-name"

private const val playerNameRegex = "player-name-regex"
private const val pPlayerRegexParam = "regex"
private const val pPlayerRegexFnameParam = "regex-first-name"
private const val pPlayerRegexLnameParam = "regex-last-name"
private const val pPlayerRegexCaseSensitive = "regex-case-sensitive"

private var debug = false

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
            appendCrumb(newCrumb)
        }
    }
}

private fun ElementCreator<*>.appendCrumb(newCrumb: Crumb) {
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
            div(fomantic.ui.three.item.menu).new {
                a(fomantic.ui.item).text("Player Name By Length").on.click {
                    browser.url.value = "/q/$playerNameLength/"
                }
                a(fomantic.ui.item).text("Player Name Search").on.click {
                    browser.url.value = "/q/$playerLastNameSearchQuery/"
                }
                a(fomantic.ui.item).text("Player Name Regex Search").on.click {
                    browser.url.value = "/q/$playerNameRegex/"
                }
            }
        }
        div(fomantic.ui.hidden.divider)
        div(fomantic.ui.container.id("errors"))
    }
    path("/q/$playerNameLength/{lengthParam}") {
        renderNavMenu(Crumb("Name Length", "/q/$playerNameLength/${parameters[playerNameLength]}"))
        div(fomantic.ui.hidden.divider)
        div(fomantic.ui.container).new {
            generatePlayerNameLengthForm(queryEngine)
            debugParamsElement(parameters)
        }

        // Handle the case if the request comes in via a GET rather than websockets
        if (!parameters[pPlayerNameLength].isNullOrEmpty()) {
            val lengthParam = parameters[pPlayerNameLength]!!
            val outputDiv = browser.doc.getElementById("output")
            PaginatedRecords(queryEngine.playerNamesByLength(lengthParam), outputDiv).renderTable()
            browser.url.value = "/q/$playerNameLength/?$pPlayerNameLength=$lengthParam"
        }

        div(fomantic.ui.hidden.divider)
        div(fomantic.ui.container.id("errors"))
    }
    path("/q/$playerLastNameSearchQuery/{nameParam}") {
        renderNavMenu(Crumb("Last Name", "/q/$playerLastNameSearchQuery/${parameters[playerLastNameSearchQuery]}"))
        div(fomantic.ui.hidden.divider)
        div(fomantic.ui.container).new {
            generatePlayerNameSearchForm(queryEngine)
            debugParamsElement(parameters)
        }

        // TODO AA -- these blocks of handling GETs should, I think, be handled better by on.keypress handlers
        //  in the input elements
        if (parameters[pPlayerLastNameParam]?.isNotEmpty()!!) {
            val lastNameParam = parameters[pPlayerLastNameParam]!!
            val outputDiv = browser.doc.getElementById("names")
            PaginatedRecords(queryEngine.playerNameSearch("%${lastNameParam.toLowerCase()}%"), outputDiv).renderTable()
            browser.url.value = "/q/$playerLastNameSearchQuery/?$pPlayerLastNameParam=$lastNameParam"
        }

        div(fomantic.ui.hidden.divider)
        div(fomantic.ui.container.id("errors"))
    }
    path("/q/$playerNameRegex/{regexParam...}") {
        renderNavMenu(Crumb("Regex Search", "/q/$playerNameRegex/${parameters[playerNameRegex]}"))
        div(fomantic.ui.hidden.divider)
        div(fomantic.ui.container).new {
            generatePlayerNameRegexSearchForm(queryEngine)
            debugParamsElement(parameters)
        }
        if (parameters[pPlayerRegexParam]?.isNotEmpty()!!) {
            val regex = parameters[pPlayerRegexParam]!!
            val useFirst = parameters[pPlayerRegexFnameParam]!!
            val useLast = parameters[pPlayerRegexLnameParam]!!
            val useCase = parameters[pPlayerRegexCaseSensitive]!!
            val outputEl = browser.doc.getElementById("names")
            PaginatedRecords(queryEngine.playerNameRegexSearch(regex, useFirst.toBoolean(), useLast.toBoolean(), useCase.toBoolean()), outputEl).renderTable()
        }
        div(fomantic.ui.hidden.divider)
        div(fomantic.ui.container.id("errors"))
    }
}

private fun ElementCreator<*>.debugParamsElement(parameters: Parameters): Element {
    return if (debug) { div(fomantic.content).text(parameters.entries().mapIndexed { idx, entry ->
        "$idx: ${entry.key}=${entry.value.map { it }}"
    }.joinToString()) } else { div() }
}

private fun ElementCreator<*>.generatePlayerNameRegexSearchForm(queries: QueryEngine) {
    val names = KVar("")
    var nameRegexInput: InputElement? = null
    var firstNameMatch: InputElement? = null
    var lastNameMatch: InputElement? = null
    var caseSensitive: InputElement? = null
    val nameRegex = KVar("")
    var useFirstName = true
    var useLastName = true
    var isCaseSensitive = true

    div(fomantic.ui.form).new {
        div(fomantic.fields).new {
            div(fomantic.inline.field).new {
                label().text("Name Regex")
                nameRegexInput = input(
                    type = InputType.text,
                    name = pPlayerRegexParam,
                    initialValue = "",
                    size = 32,
                    placeholder = """ Regex, e.g. '.(?:na){2}.*' """.trim()
                )
                nameRegexInput?.value = nameRegex
            }
            div(fomantic.inline.field).new {
                label().text("First Name")
                firstNameMatch = input(
                    type = InputType.checkbox,
                    name = pPlayerRegexFnameParam
                )
                firstNameMatch!!.checked(useFirstName)
                firstNameMatch!!.on.click {
                    useFirstName = !useFirstName
                    firstNameMatch!!.checked(useFirstName)
                }
            }
            div(fomantic.inline.field).new {
                label().text("Last Name")
                lastNameMatch = input(
                    type = InputType.checkbox,
                    name = pPlayerRegexLnameParam
                )
                lastNameMatch!!.checked(useLastName)
                lastNameMatch!!.on.click {
                    useLastName = !useLastName
                    lastNameMatch!!.checked(useLastName)
                }
            }
            div(fomantic.inline.field).new {
                label().text("Case sensitive")
                caseSensitive = input(
                    type = InputType.checkbox,
                    name = pPlayerRegexCaseSensitive
                )
                caseSensitive!!.checked(isCaseSensitive)
                caseSensitive!!.on.click {
                    isCaseSensitive = !isCaseSensitive
                    caseSensitive!!.checked(isCaseSensitive)
                }
            }
            button(fieldButtonStyle).text("Search").on.click {
                GlobalScope.launch {
                    getInputAndRenderResult(
                        listOf(nameRegexInput!!, firstNameMatch!!, lastNameMatch!!, caseSensitive!!),
                        browser.doc.getElementById("names")
                    ) { inputs -> queries.playerNameRegexSearch(
                            inputs[0],
                            inputs[1].isNotEmpty(),
                            inputs[2].isNotEmpty(),
                            inputs[3].isNotEmpty()
                        )
                    }

                    browser.url.value = "/q/$playerNameRegex/?$pPlayerRegexParam=${nameRegexInput!!.getValue().await()}&" +
                            "$pPlayerRegexFnameParam=${firstNameMatch!!.getValue().await()}&" +
                            "$pPlayerRegexLnameParam=${lastNameMatch!!.getValue().await()}&" +
                            "$pPlayerRegexCaseSensitive=${caseSensitive!!.getValue().await()}"

                }
            }
        }
    }

    div(fomantic.ui.hidden.divider)
    div(fomantic.content.id("names")).text(names)
}

private fun ElementCreator<*>.generatePlayerNameSearchForm(queries: QueryEngine) {
    val output = KVar("")
    var nameFragmentInput: InputElement? = null

    div(fomantic.ui.form).new {
        div(fomantic.fields).new {
            div(fomantic.six.wide.inline.field).new {
                label().text("Last Name")
                nameFragmentInput = input(
                    type = InputType.text,
                    name = pPlayerLastNameParam,
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
                    browser.url.value = "/q/$playerLastNameSearchQuery/?$pPlayerLastNameParam=${nameFragmentInput!!.getValue().await()}"
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

    div(fomantic.ui.form).new {
        div(fomantic.fields).new {
            div(fomantic.inline.field).new {
                label().text("Name Length")
                nameLengthInput = input(
                    type = InputType.text,
                    name = pPlayerNameLength,
                    initialValue = "",
                    size = 15,
                    placeholder = "14"
                )
            }
            button(fieldButtonStyle).text("Search").on.click {
                GlobalScope.launch {
                    getInputAndRenderResult(listOf(nameLengthInput!!),
                        browser.doc.getElementById("output")) {
                        inputs -> queries.playerNamesByLength(inputs[0])
                    }
                    browser.url.value = "/q/$playerNameLength/?$pPlayerNameLength=${nameLengthInput!!.getValue().await()}"
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
        when (it.read.attribute("type").await()) {
            InputType.text.name -> {
                params.add(it.getValue().await())
            }
            InputType.checkbox.name -> {
                params.add(it.read.attribute("checked").await().toString())
            }
            else -> params.add("unknown, id: ${it.id!!}")
        }
    }
    getQueryResult(params, outputElement, query)
}

private fun getQueryResult(
    params: List<String>,
    outputElement: Element,
    query: (inputs: List<String>) -> List<TableRecord>
) {
    PaginatedRecords(query(params), outputElement).renderTable()
}
