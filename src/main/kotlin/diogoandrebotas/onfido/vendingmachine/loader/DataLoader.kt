package diogoandrebotas.onfido.vendingmachine.loader

import diogoandrebotas.onfido.vendingmachine.model.Change
import diogoandrebotas.onfido.vendingmachine.model.Product
import diogoandrebotas.onfido.vendingmachine.repository.ChangeRepository
import diogoandrebotas.onfido.vendingmachine.repository.ProductRepository
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class DataLoader(
    private val productRepository: ProductRepository,
    private val changeRepository: ChangeRepository
) {

    @PostConstruct
    fun loadProducts() {
        val products = productList()
        productRepository.saveAll(products)
    }

    @PostConstruct
    fun loadChange() {
        val coins = listOf(
            Change("£2", 5),
            Change("£1", 10),
            Change("50p", 20),
            Change("20p", 50),
            Change("10p", 100),
            Change("5p", 200),
            Change("2p", 500),
            Change("1p", 1000),
        )

        changeRepository.saveAll(coins)
    }

    private fun productList(): List<Product> {
        return listOf(
            Product(
                name = "Plant Based Protein Bar, Caramel",
                price = "£1.99",
                availableQuantity = 10
            ),
            Product(
                name = "Plant Based Protein Bar, Strawberry",
                price = "£1.85",
                availableQuantity = 10
            ),
            Product(
                name = "Coke Zero, Lime",
                price = "£2",
                availableQuantity = 10
            ),
            Product(
                name = "Fanta Zero, Orange",
                price = "99p",
                availableQuantity = 10
            ),
            Product(
                name = "Lay's, Potato Chips, Classic",
                price = "£2.5",
                availableQuantity = 10
            ),
        )
    }

}