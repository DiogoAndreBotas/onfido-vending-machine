package diogoandrebotas.onfido.vendingmachine.model.http

import diogoandrebotas.onfido.vendingmachine.model.CoinAndQuantity
import kotlinx.serialization.Serializable

@Serializable
data class ProductPurchaseRequestBody(
    val coins: List<CoinAndQuantity>
)
