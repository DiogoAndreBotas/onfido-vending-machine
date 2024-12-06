package diogoandrebotas.onfido.vendingmachine.model.http

import diogoandrebotas.onfido.vendingmachine.model.Product
import diogoandrebotas.onfido.vendingmachine.model.TempChangeStruct

data class ProductPurchaseResponseBody(
    val product: Product,
    val change: List<TempChangeStruct>
)
