package diogoandrebotas.onfido.vendingmachine.model

data class ProductAndChange(
    val product: Product,
    val change: List<CoinAndQuantity>
)
