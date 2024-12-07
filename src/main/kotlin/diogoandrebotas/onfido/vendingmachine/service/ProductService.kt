package diogoandrebotas.onfido.vendingmachine.service

import diogoandrebotas.onfido.vendingmachine.exception.NotEnoughMoneyProvidedException
import diogoandrebotas.onfido.vendingmachine.exception.ProductNotFoundException
import diogoandrebotas.onfido.vendingmachine.exception.ProductOutOfStockException
import diogoandrebotas.onfido.vendingmachine.exception.CoinNotAcceptedException
import diogoandrebotas.onfido.vendingmachine.model.Coin
import diogoandrebotas.onfido.vendingmachine.model.CoinQuantity
import diogoandrebotas.onfido.vendingmachine.model.Product
import diogoandrebotas.onfido.vendingmachine.model.ProductAndChange
import diogoandrebotas.onfido.vendingmachine.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val changeService: ChangeService
) {

    fun getProducts(): List<Product> = productRepository.findAll()

    fun getProduct(id: Long): Product = productRepository.findById(id).orElseThrow { ProductNotFoundException(id) }

    fun purchaseProduct(id: Long, coinQuantities: List<CoinQuantity>): ProductAndChange {
        validateCoins(coinQuantities.map { it.coin })

        val product = getProduct(id)
        if (product.availableQuantity == 0) throw ProductOutOfStockException(product.name)

        val totalValue = coinQuantities.sumOf {
            calculateCoinValue(it.coin).times(it.quantity)
        }

        val productPrice = calculateCoinValue(product.price)

        if (totalValue >= productPrice) {
            val updatedProduct = decreaseProductQuantity(product)

            val change = if ((totalValue - productPrice) > 0) {
                changeService.calculateChange(totalValue - productPrice)
            } else {
                emptyList()
            }

            return ProductAndChange(updatedProduct, change)
        }
        else {
            throw NotEnoughMoneyProvidedException(productPrice, totalValue)
        }
    }

    fun resetProductQuantities(): List<Product> {
        val productsToUpdate = productRepository.findAll()

        productsToUpdate.forEach { it.availableQuantity = 10 }

        return productRepository.saveAll(productsToUpdate)
    }

    private fun validateCoins(coins: List<String>) {
        val acceptedCoins = Coin.entries.map { it.coin }.toSet()

        coins.forEach { coin ->
            if (acceptedCoins.none { acceptedCoin -> acceptedCoin == coin }) {
                throw CoinNotAcceptedException(coin)
            }
        }
    }

    private fun calculateCoinValue(coin: String): Double {
        return if (coin.endsWith("p")) {
            coin.removeSuffix("p").toDouble().div(100)
        } else {
            coin.removePrefix("£").toDouble()
        }
    }

    private fun decreaseProductQuantity(product: Product): Product {
        product.availableQuantity -= 1
        return productRepository.save(product)
    }

}