package diogoandrebotas.onfido.vendingmachine.model.http

import diogoandrebotas.onfido.vendingmachine.model.CoinQuantity
import kotlinx.serialization.Serializable

@Serializable
data class PurchaseRequestBody(
    val coins: List<CoinQuantity>
)
