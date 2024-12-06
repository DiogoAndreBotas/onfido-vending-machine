package diogoandrebotas.onfido.vendingmachine.service

import diogoandrebotas.onfido.vendingmachine.model.Product
import diogoandrebotas.onfido.vendingmachine.model.TempChangeStruct
import diogoandrebotas.onfido.vendingmachine.model.http.CoinQuantity
import diogoandrebotas.onfido.vendingmachine.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val changeService: ChangeService
) {

    fun getProducts(): List<Product> = productRepository.findAll()

    fun getProduct(id: Long): Product = productRepository.findById(id).get()

    fun purchaseProduct(id: Long, coinQuantities: List<CoinQuantity>): Pair<Product, List<TempChangeStruct>> {
        val product = getProduct(id)

        if (product.availableQuantity == 0) {
            throw Exception("This product is out of stock")
        }

        // TODO: can be a reduce operation
        // TODO: probably can be a private method
        val totalValue = coinQuantities.map {
            val coinValue = if (it.coin.endsWith("p")) {
                it.coin.removeSuffix("p").toFloat().div(100)
            } else {
                it.coin.removePrefix("£").toFloat()
            }

            coinValue.times(it.quantity)
        }.sum()

        val productPrice = if (product.price.endsWith("p")) {
            product.price.removeSuffix("p").toFloat().div(10)
        } else {
            product.price.removePrefix("£").toFloat()
        }
        if (totalValue >= productPrice) {
            product.availableQuantity -= 1
            val updatedProduct = productRepository.save(product)

            val change = if ((totalValue - productPrice) > 0) {
                changeService.calculateChange(totalValue - productPrice)
            } else { emptyList() }

            return Pair(updatedProduct, change)
        }
        else {
            throw Exception("not enough money")
        }
    }

}