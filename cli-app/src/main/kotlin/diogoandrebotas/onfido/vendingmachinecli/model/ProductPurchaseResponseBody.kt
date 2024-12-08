package diogoandrebotas.onfido.vendingmachinecli.model

data class ProductPurchaseResponseBody(
    val product: ProductResponse,
    val change: List<CoinAndQuantity>
)

data class CoinAndQuantity(
    val coin: String,
    val quantity: Int
)