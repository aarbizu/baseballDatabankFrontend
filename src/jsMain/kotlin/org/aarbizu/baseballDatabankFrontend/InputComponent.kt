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
