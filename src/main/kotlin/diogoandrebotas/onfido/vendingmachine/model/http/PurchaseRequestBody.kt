package diogoandrebotas.onfido.vendingmachine.model.http

data class PurchaseRequestBody(
    val coins: List<CoinQuantity>
)

data class CoinQuantity(
    val coin: String,
    val quantity: Int
)