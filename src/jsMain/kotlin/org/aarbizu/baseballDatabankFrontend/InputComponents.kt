package org.aarbizu.baseballDatabankFrontend

import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import react.FC
import react.Props
import react.dom.events.ChangeEventHandler
import react.dom.events.FormEventHandler
import react.dom.html.InputType
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.useState

external interface InputProps : Props {
    var onSubmit: (String) -> Unit
    var inputLabel: String
    var allowedPattern: String
    var title: String
}

external interface NameSearchProps : Props {
    var onSubmit: (PlayerNameSearchParam) -> Unit
    var textLabel: String
    var title: String
}

val InputComponent =
    FC<InputProps> { props ->
        val (text, setText) = useState("")

        val submitHandler: FormEventHandler<HTMLFormElement> = {
            it.preventDefault()
            setText("")
            props.onSubmit(text)
        }

        val changeHandler: ChangeEventHandler<HTMLInputElement> = { setText(it.target.value) }

        form {
            onSubmit = submitHandler
            if (props.inputLabel.isNotBlank()) label { +props.inputLabel }
            input {
                type = InputType.text
                onChange = changeHandler
                value = text
                if (props.allowedPattern.isNotBlank()) pattern = props.allowedPattern
                if (props.title.isNotBlank()) title = props.title
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

        val nameChangeHandler: ChangeEventHandler<HTMLInputElement> = {
            setNameSearchString(it.target.value)
        }
        val firstNameMatchChangeHdlr: ChangeEventHandler<HTMLInputElement> = {
            setFirstNameMatch(it.target.checked)
        }
        val lastNameMatchChangeHdlr: ChangeEventHandler<HTMLInputElement> = {
            setLastNameMatch(it.target.checked)
        }
        val caseSensitiveMatchChangeHdlr: ChangeEventHandler<HTMLInputElement> = {
            setCaseSensitiveMatch(it.target.checked)
        }

        val submitHandler: FormEventHandler<HTMLFormElement> = {
            it.preventDefault()
            setLastSearch(nameSearchString)
            props.onSubmit(
                PlayerNameSearchParam(
                    nameSearchString = nameSearchString,
                    matchFirstName = firstNameMatchToggle,
                    matchLastName = lastNameMatchToggle,
                    caseSensitive = caseSensitiveMatchToggle
                )
            )
        }

        form {
            onSubmit = submitHandler
            if (props.textLabel.isNotBlank()) label { +props.textLabel }
            input {
                type = InputType.text
                onChange = nameChangeHandler
                value = nameSearchString
            }
            +"Match first"
            input {
                type = InputType.checkbox
                onChange = firstNameMatchChangeHdlr
                checked = firstNameMatchToggle
            }
            +"Match last"
            input {
                type = InputType.checkbox
                onChange = lastNameMatchChangeHdlr
                checked = lastNameMatchToggle
            }
            +"Case sensitive"
            input {
                type = InputType.checkbox
                onChange = caseSensitiveMatchChangeHdlr
                checked = caseSensitiveMatchToggle
            }
            +"Previous Search: $lastSearch"
        }
    }
