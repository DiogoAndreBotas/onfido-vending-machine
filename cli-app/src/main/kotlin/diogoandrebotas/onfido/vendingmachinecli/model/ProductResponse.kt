package diogoandrebotas.onfido.vendingmachinecli.model

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: String,
    var availableQuantity: Int
) {

    override fun toString(): String {
        return "$id - $name - $price - Quantity available: $availableQuantity"
    }

}