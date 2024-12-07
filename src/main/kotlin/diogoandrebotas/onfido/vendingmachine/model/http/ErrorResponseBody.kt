package diogoandrebotas.onfido.vendingmachine.model.http

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseBody(
    val status: Int,
    val message: String
)
