package diogoandrebotas.onfido.vendingmachine.model.http

import diogoandrebotas.onfido.vendingmachine.model.CoinQuantity

data class PurchaseRequestBody(
    val coins: List<CoinQuantity>
)
