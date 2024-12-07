package diogoandrebotas.onfido.vendingmachine.model.http

import diogoandrebotas.onfido.vendingmachine.model.CoinQuantity
import diogoandrebotas.onfido.vendingmachine.model.Product

data class ProductPurchaseResponseBody(
    val product: Product,
    val change: List<CoinQuantity>
)
