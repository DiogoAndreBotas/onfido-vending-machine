package diogoandrebotas.onfido.vendingmachine.model

import kotlinx.serialization.Serializable

@Serializable
data class CoinQuantity(
    val coin: String,
    val quantity: Int
)
