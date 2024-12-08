package diogoandrebotas.onfido.vendingmachine.model

import kotlinx.serialization.Serializable

@Serializable
data class CoinAndQuantity(
    val coin: String,
    val quantity: Int
)
