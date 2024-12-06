package diogoandrebotas.onfido.vendingmachine.service

import diogoandrebotas.onfido.vendingmachine.exception.NotEnoughMoneyProvidedException
import diogoandrebotas.onfido.vendingmachine.exception.ProductNotFoundException
import diogoandrebotas.onfido.vendingmachine.exception.ProductOutOfStockException
import diogoandrebotas.onfido.vendingmachine.exception.UnrecognizedCoinException
import diogoandrebotas.onfido.vendingmachine.model.Coin
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

    fun getProduct(id: Long): Product = productRepository.findById(id).orElseThrow { ProductNotFoundException(id) }

    fun purchaseProduct(id: Long, coinQuantities: List<CoinQuantity>): Pair<Product, List<TempChangeStruct>> {
        validateCoins(coinQuantities.map { it.coin })

        val product = getProduct(id)
        if (product.availableQuantity == 0) throw ProductOutOfStockException(product.name)

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
            throw NotEnoughMoneyProvidedException(productPrice, totalValue)
        }
    }

    fun resetProductQuantities(): List<Product> {
        val updatedProducts = productRepository.findAll().map {
            it.availableQuantity = 10
            it
        }

        return productRepository.saveAll(updatedProducts)
    }

    private fun validateCoins(coins: List<String>) {
        val acceptedCoins = Coin.entries.map { it.coin }.toSet()

        coins.forEach { coin ->
            if (acceptedCoins.none { acceptedCoin -> acceptedCoin == coin }) {
                throw UnrecognizedCoinException(coin)
            }
        }
    }

}