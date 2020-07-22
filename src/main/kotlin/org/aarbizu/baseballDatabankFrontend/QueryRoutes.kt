package org.aarbizu.baseballDatabankFrontend

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
import kweb.table
import kweb.tbody
import kweb.td
import kweb.th
import kweb.thead
import kweb.tr

private val massiveButtonStyle = mapOf("class" to "ui massive orange right labeled icon button")
private val buttonStyle = mapOf("class" to "ui large orange right labeled icon button")
private val baseballGlyphStyle = mapOf("class" to "baseball ball icon")
private val subHeaderStyle = mapOf("class" to "sub header")
private val homeIcon = mapOf("class" to "home icon")

private const val playerNameLengthQuery = "player-name-by-len"
private const val playerNameSearchQuery = "player-name"

fun RouteReceiver.getRoutePaths() {
    path("/") {
        renderNavMenu()
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
                        .text("Visit the Chadwick Bureau")
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
    path("/q/{type}") { _ ->
        renderNavMenu()
        div(fomantic.ui.hidden.divider)
        div(fomantic.ui.container).new {
            div(fomantic.ui.two.item.menu).new {
                a(fomantic.ui.item).text("Player Name By Length").on.click {
                    val queryElement = browser.doc.getElementById("query")
                    handleQuery(playerNameLengthQuery, queryElement)
                    browser.url.value = "/q/$playerNameLengthQuery"
                }
                a(fomantic.ui.item).text("Player Name Search").on.click {
                    val queryElement = browser.doc.getElementById("query")
                    handleQuery(playerNameSearchQuery, queryElement)
                    browser.url.value = "/q/$playerNameSearchQuery"
                }
            }

            div(fomantic.ui.container.id("query"))

//            button(buttonStyle).text("Search").on.click {
//                val queryType = params.getValue("type").value
//                val queryElement = browser.doc.getElementById("query")
//                val nextQueryType = handleQuery(queryType, queryElement)
//                browser.url.value = "/q/$nextQueryType"
//            }.new {
//                i(baseballGlyphStyle)
//            }
        }
    }
}

private fun ElementCreator<*>.renderNavMenu() {
    div(fomantic.ui.top.attached.menu.inverted).new {
        a(href = "/").new {
            div(fomantic.ui.item).new {
                i(homeIcon)
            }
        }
    }
}

private fun handleQuery(queryType: String, element: Element) {
    if (queryType == "begin") {
        element.removeChildren().new {
            table().new {
                thead().new {
                    tr().new {
                        th().text("Name"); th().text("Position")
                    }
                }
                tbody().new {
                    tr().new {
                        td().text("Bonds"); td().text("The G.O.A.T.")
                    }
                }
            }
        }
    }

    if (queryType == playerNameLengthQuery) {
        generatePlayerNameLengthForm(element)
    }

    if (queryType == playerNameSearchQuery) {
        element.removeChildren().new {
            div(fomantic.content).text("insert player name search query")
        }
    }
}

private fun generatePlayerNameLengthForm(element: Element) {
    val output = KVar("")
    var length: String
    var nameLengthInput: InputElement? = null
    element.removeChildren().new {
        form(fomantic.ui.form).new {
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
            button(attributes = buttonStyle).text("Search").on.click {
                GlobalScope.launch {
                    length = nameLengthInput!!.getValue().await()
                    output.value = length
                }
            }.new {
                i(baseballGlyphStyle)
            }
        }
        div(fomantic.ui.hidden.divider)
        div(fomantic.content.id("output")).text(output)
    }
}
