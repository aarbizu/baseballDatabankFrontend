package org.aarbizu.baseballDatabankFrontend

import csstype.Auto
import csstype.pct
import kotlinx.js.jso
import mui.material.Box
import mui.material.FormControl
import mui.material.FormLabel
import mui.material.TextField
import mui.system.sx
import org.w3c.dom.HTMLInputElement
import react.FC
import react.Props
import react.dom.events.FormEventHandler
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.legend
import react.dom.onChange
import react.useState

external interface TextInputProps : Props {
    var onSubmit: (String) -> Unit
    var inputLabel: String
    var allowedPattern: String
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

        val submitHandler: FormEventHandler<*> = {
            it.preventDefault()
            setTextInput("")
            if (textInputValue.isNotBlank()) {
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
                    inputProps = jso {
                        sx {
                            """
                                pattern="${props.allowedPattern}"
                            """.trimIndent()
                        }
                    }
                    placeholder = props.placeHolderString
                    value = textInputValue
                    onChange = { event ->
                        val target = event.target as HTMLInputElement
                        setTextInput(target.value)
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
