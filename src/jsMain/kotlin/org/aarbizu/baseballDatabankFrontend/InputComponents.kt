package org.aarbizu.baseballDatabankFrontend

import csstype.Auto
import csstype.pct
import kotlinx.js.jso
import mui.material.Alert
import mui.material.AlertColor
import mui.material.AlertTitle
import mui.material.Box
import mui.material.FormControl
import mui.material.FormLabel
import mui.material.TextField
import mui.system.sx
import org.w3c.dom.HTMLInputElement
import react.FC
import react.Props
import react.dom.events.FormEventHandler
import react.dom.html.HTMLAttributes
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.legend
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
                        AlertTitle {
                            +"Error: Invalid input"
                        }
                        +"Try another value, please."
                    }
                }
            }
        }
    }

val NameSearchInput =
    FC<NameSearchProps> { props ->
        //        val (nameSearchString, setNameSearchString) = useState("")
        //        val (firstNameMatchToggle, setFirstNameMatch) = useState(false)
        //        val (lastNameMatchToggle, setLastNameMatch) = useState(false)
        //        val (caseSensitiveMatchToggle, setCaseSensitiveMatch) = useState(false)
        //        val (lastSearch, setLastSearch) = useState("")
        //
        //        val nameChangeHandler: ChangeEventHandler<HTMLInputElement> = {
        //            setNameSearchString(it.target.value)
        //        }
        //        val firstNameMatchChangeHdlr: ChangeEventHandler<HTMLInputElement> = {
        //            setFirstNameMatch(it.target.checked)
        //        }
        //        val lastNameMatchChangeHdlr: ChangeEventHandler<HTMLInputElement> = {
        //            setLastNameMatch(it.target.checked)
        //        }
        //        val caseSensitiveMatchChangeHdlr: ChangeEventHandler<HTMLInputElement> = {
        //            setCaseSensitiveMatch(it.target.checked)
        //        }
        //
        //        val submitHandler: FormEventHandler<HTMLFormElement> = {
        //            it.preventDefault()
        //            setLastSearch(nameSearchString)
        //            props.onSubmit(
        //                PlayerNameSearchParam(
        //                    nameSearchString = nameSearchString,
        //                    matchFirstName = firstNameMatchToggle,
        //                    matchLastName = lastNameMatchToggle,
        //                    caseSensitive = caseSensitiveMatchToggle
        //                )
        //            )
        //        }
        //
        //        form {
        //            onSubmit = submitHandler
        //            if (props.textLabel.isNotBlank()) label { +props.textLabel }
        //            input {
        //                type = InputType.text
        //                onChange = nameChangeHandler
        //                value = nameSearchString
        //            }
        //            +"Match first"
        //            input {
        //                type = InputType.checkbox
        //                onChange = firstNameMatchChangeHdlr
        //                checked = firstNameMatchToggle
        //            }
        //            +"Match last"
        //            input {
        //                type = InputType.checkbox
        //                onChange = lastNameMatchChangeHdlr
        //                checked = lastNameMatchToggle
        //            }
        //            +"Case sensitive"
        //            input {
        //                type = InputType.checkbox
        //                onChange = caseSensitiveMatchChangeHdlr
        //                checked = caseSensitiveMatchToggle
        //            }
        //            +"Previous Search: $lastSearch"
        //        }
    }
