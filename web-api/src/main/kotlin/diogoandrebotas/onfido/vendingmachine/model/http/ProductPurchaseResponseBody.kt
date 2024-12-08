package diogoandrebotas.onfido.vendingmachine.model.http

import diogoandrebotas.onfido.vendingmachine.model.CoinAndQuantity
import diogoandrebotas.onfido.vendingmachine.model.Product
import kotlinx.serialization.Serializable

@Serializable
data class ProductPurchaseResponseBody(
    val product: Product,
    val change: List<CoinAndQuantity>
)
