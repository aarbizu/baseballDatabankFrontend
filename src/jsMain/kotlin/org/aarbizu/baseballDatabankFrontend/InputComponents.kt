package org.aarbizu.baseballDatabankFrontend

import mui.material.Alert
import mui.material.AlertColor
import mui.material.AlertTitle
import mui.material.Checkbox
import mui.material.FormControl
import mui.material.FormControlLabel
import mui.material.FormGroup
import mui.material.FormLabel
import mui.material.Paper
import mui.material.Radio
import mui.material.RadioGroup
import mui.material.Stack
import mui.material.TextField
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.dom.events.FormEventHandler
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.span
import react.dom.onChange
import react.useState
import web.cssom.Auto
import web.cssom.FlexDirection
import web.cssom.em
import web.html.HTMLInputElement

external interface NameLengthProps : Props {
    var onSubmit: (String, String) -> Unit
    var inputLabel: String
    var allowedPattern: (String) -> Boolean
    var title: String
    var placeHolderString: String
}

external interface NameSearchProps : Props {
    var onSubmit: (PlayerNameSearchParam) -> Unit
    var textLabel: String
}

val NameLengthInputComponents =
    FC<NameLengthProps> { props ->
        val (textInputValue, setTextInput) = useState("")
        val (validInput, setValidInput) = useState(true)
        val (nameOption, setNameOption) = useState("checkLast")

        val submitHandler: FormEventHandler<*> = {
            it.preventDefault()
            setTextInput("")
            if (textInputValue.isNotBlank() && validInput) {
                props.onSubmit(textInputValue, nameOption)
            }
        }

        Stack {
            FormControl {
                component = form
                onSubmit = submitHandler

                FormLabel {
                    sx { marginLeft = Auto.auto }
                    +props.title
                }

                TextField {
                    sx { marginLeft = Auto.auto }
                    placeholder = props.placeHolderString
                    value = textInputValue
                    onChange = { event ->
                        setValidInput(true)
                        val target = event.target.asDynamic().unsafeCast<HTMLInputElement>()
                        setTextInput(target.value)
                        if (!props.allowedPattern(target.value)) setValidInput(false)
                    }
                }

                RadioGroup {
                    sx {
                        flexDirection = FlexDirection.row
                        marginLeft = Auto.auto
                    }

                    defaultValue = "checkLast"
                    name = "name-length-options"
                    row = true
                    value = nameOption
                    onChange = { _, value -> setNameOption(value) }

                    Typography {
                        sx { padding = 1.em }
                        variant = TypographyVariant.subtitle1
                        component = span
                        +"Length Options:"
                    }
                    FormControlLabel {
                        label = ReactNode("Last")
                        value = "checkLast"
                        control = Radio.create()
                    }
                    FormControlLabel {
                        label = ReactNode("First")
                        value = "checkFirst"
                        control = Radio.create()
                    }
                    FormControlLabel {
                        label = ReactNode("First+Last")
                        value = "checkFirstLast"
                        control = Radio.create()
                    }
                    FormControlLabel {
                        label = ReactNode("Full")
                        value = "checkFull"
                        control = Radio.create()
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
                    caseSensitive = caseSensitiveMatchToggle,
                ),
            )
        }

        Stack {
            FormControl {
                component = form
                onSubmit = submitHandler

                TextField {
                    placeholder = "name or regex"
                    value = nameSearchString
                    sx { marginLeft = Auto.auto }
                    onChange = { event ->
                        val target = event.target.asDynamic().unsafeCast<HTMLInputElement>()
                        setNameSearchString(target.value)
                    }
                }

                FormGroup {
                    sx {
                        flexDirection = FlexDirection.row
                        marginLeft = Auto.auto
                    }
                    Typography {
                        sx { padding = 1.em }
                        variant = TypographyVariant.subtitle1
                        component = span
                        +"Match:"
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
                        label = ReactNode("First")
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
                        label = ReactNode("Last")
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
                        label = ReactNode("Case")
                    }
                }
                Paper {
                    elevation = 0
                    sx { marginLeft = Auto.auto }
                    Typography {
                        variant = TypographyVariant.subtitle1
                        component = span
                        +"Last search = $lastSearch"
                    }
                }
            }
        }
    }
