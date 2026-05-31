package kermes.proxy.server.models

import kotlinx.serialization.Serializable

@Serializable
data class FormInput(
    val id: String,
    val type: String,
    val label: String,
    val maxLength: Int? = null
)

@Serializable
data class FormInputs(
    val type: String,
    val inputs: List<FormInput>
)

val loginForm = FormInputs(
    type = "LOGIN_FORM",
    inputs = listOf(
        FormInput(
            id = "account_name",
            type = "text",
            label = "E-mail",
            maxLength = 320
        ),
        FormInput(
            id = "password",
            type = "password",
            label = "Password",
            maxLength = 16
        ),
        FormInput(
            id = "log_in_submit",
            type = "submit",
            label = "Log In"
        )
    )
)
