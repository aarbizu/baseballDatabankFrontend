package org.aarbizu.baseballDatabankFrontend

import csstype.Auto
import csstype.FlexDirection
import csstype.em
import csstype.pct
import mui.material.Alert
import mui.material.AlertColor
import mui.material.AlertTitle
import mui.material.Box
import mui.material.Checkbox
import mui.material.FormControl
import mui.material.FormControlLabel
import mui.material.FormGroup
import mui.material.FormLabel
import mui.material.MuiList.Companion.padding
import mui.material.Paper
import mui.material.TextField
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import org.w3c.dom.HTMLInputElement
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.dom.events.FormEventHandler
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.legend
import react.dom.html.ReactHTML.span
import react.dom.onChange
import react.useState

external interface TextInputProps : Props {
    var onSubmit: (String) -> Unit
    var inputLabel: String
    var allowedPattern: (String) -> Boolean
    var title: String
    var placeHolderString: String
}

external interface NameSearchProps : Props {
    var onSubmit: (PlayerNameSearchParam) -> Unit
    var textLabel: String
    var title: String
}

val TextInputComponent =
    FC<TextInputProps> { props ->
        val (textInputValue, setTextInput) = useState("")
        val (validInput, setValidInput) = useState(true)

        val submitHandler: FormEventHandler<*> = {
            it.preventDefault()
            setTextInput("")
            if (textInputValue.isNotBlank() && validInput) {
                props.onSubmit(textInputValue)
            }
        }

        Box {
            component = div
            sx {
                marginLeft = Auto.auto
                marginRight = Auto.auto
                width = 70.pct
            }
            FormControl {
                component = form
                onSubmit = submitHandler

                FormLabel {
                    component = legend
                    +props.title
                }

                TextField {
                    placeholder = props.placeHolderString
                    value = textInputValue
                    onChange = { event ->
                        setValidInput(true)
                        val target = event.target as HTMLInputElement
                        setTextInput(target.value)
                        if (!props.allowedPattern(target.value)) setValidInput(false)
                    }
                }

                if (!validInput) {
                    Alert {
                        severity = AlertColor.error
                        AlertTitle { +"Error: Invalid input" }
                        +"Try another value, please."
                    }
                }
            }
        }
    }

val NameSearchInput =
    FC<NameSearchProps> { props ->
        val (nameSearchString, setNameSearchString) = useState("")
        val (firstNameMatchToggle, setFirstNameMatch) = useState(false)
        val (lastNameMatchToggle, setLastNameMatch) = useState(false)
        val (caseSensitiveMatchToggle, setCaseSensitiveMatch) = useState(false)
        val (lastSearch, setLastSearch) = useState("")

        val submitHandler: FormEventHandler<*> = {
            it.preventDefault()
            setLastSearch(nameSearchString)
            setNameSearchString("")
            props.onSubmit(
                PlayerNameSearchParam(
                    nameSearchString = nameSearchString,
                    matchFirstName = firstNameMatchToggle,
                    matchLastName = lastNameMatchToggle,
                    caseSensitive = caseSensitiveMatchToggle
                )
            )
        }
        Box {
            component = div
            sx {
                marginLeft = Auto.auto
                marginRight = Auto.auto
                width = 70.pct
            }

            FormControl {
                component = form
                onSubmit = submitHandler

                Box {
                    FormLabel {
                        component = legend
                        +props.title
                    }
                    TextField {
                        placeholder = "name or regex"
                        value = nameSearchString
                        onChange = { event ->
                            val target = event.target as HTMLInputElement
                            setNameSearchString(target.value)
                        }
                    }
                }

                FormGroup {
                    sx { flexDirection = FlexDirection.row }
                    Typography {
                        sx { padding = 1.em }
                        variant = TypographyVariant.subtitle1
                        component = span
                        +"Match Options:"
                    }
                    FormControlLabel {
                        control =
                            FC<Props> {
                                Checkbox {
                                    onChange = { _, nextState -> setFirstNameMatch(nextState) }
                                    value = firstNameMatchToggle
                                    checked = firstNameMatchToggle
                                }
                            }
                                .create()
                        label = ReactNode("First name")
                    }
                    FormControlLabel {
                        control =
                            FC<Props> {
                                Checkbox {
                                    onChange = { _, nextState -> setLastNameMatch(nextState) }
                                    value = lastNameMatchToggle
                                    checked = lastNameMatchToggle
                                }
                            }
                                .create()
                        label = ReactNode("Last Name")
                    }
                    FormControlLabel {
                        control =
                            FC<Props> {
                                Checkbox {
                                    onChange = { _, nextState ->
                                        setCaseSensitiveMatch(nextState)
                                    }
                                    value = caseSensitiveMatchToggle
                                    checked = caseSensitiveMatchToggle
                                }
                            }
                                .create()
                        label = ReactNode("Case Sensitive")
                    }
                }
                Paper {
                    sx { padding = 0.1.em }
                    Typography {
                        variant = TypographyVariant.subtitle1
                        component = span
                        +"Last search = $lastSearch"
                    }
                }
            }
        }
    }
