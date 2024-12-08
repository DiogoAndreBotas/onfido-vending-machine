package diogoandrebotas.onfido.vendingmachine.service

import diogoandrebotas.onfido.vendingmachine.exception.CoinNotAcceptedException
import diogoandrebotas.onfido.vendingmachine.exception.NotEnoughMoneyProvidedException
import diogoandrebotas.onfido.vendingmachine.exception.ProductNotFoundException
import diogoandrebotas.onfido.vendingmachine.exception.ProductOutOfStockException
import diogoandrebotas.onfido.vendingmachine.model.CoinAndQuantity
import diogoandrebotas.onfido.vendingmachine.model.Product
import diogoandrebotas.onfido.vendingmachine.repository.ProductRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class ProductServiceTest {

    @Test
    fun `getProducts returns products`() {
        val productRepository = mock<ProductRepository> {
            on { findAll() } doReturn listOf(
                Product(
                    name = "Plant Based Protein Bar, Caramel",
                    price = "£1.99",
                    availableQuantity = 10
                )
            )
        }

        val products = ProductService(productRepository, mock<ChangeService>()).getProducts()

        val expectedProducts = listOf(
            Product(
                name = "Plant Based Protein Bar, Caramel",
                price = "£1.99",
                availableQuantity = 10
            )
        )

        assertEquals(expectedProducts, products)
    }

    @Test
    fun `getProduct returns the product`() {
        val productRepository = mock<ProductRepository> {
            on { findById(1) } doReturn Optional.of(
                Product(
                    name = "Plant Based Protein Bar, Caramel",
                    price = "£1.99",
                    availableQuantity = 10
                )
            )
        }

        val product = ProductService(productRepository, mock<ChangeService>()).getProduct(1)

        val expectedProduct = Product(
            name = "Plant Based Protein Bar, Caramel",
            price = "£1.99",
            availableQuantity = 10
        )

        assertEquals(expectedProduct, product)
    }

    @Test
    fun `getProduct throws an exception if the product does not exist`() {
        val productRepository = mock<ProductRepository> {
            on { findById(1) } doReturn Optional.empty()
        }

        assertThrows<ProductNotFoundException> {
            ProductService(productRepository, mock<ChangeService>()).getProduct(1)
        }
    }

    @Test
    fun `purchaseProduct returns the product and the change`() {
        val change = 5 - 1.99
        val productRepository = mock<ProductRepository> {
            on { findById(1) } doReturn Optional.of(
                Product(
                    name = "Plant Based Protein Bar, Caramel",
                    price = "£1.99",
                    availableQuantity = 10
                )
            )
            on { save(any<Product>()) } doAnswer { it.getArgument(0) }
        }
        val changeService = mock<ChangeService> {
            on { calculateChange(change) } doReturn listOf(
                CoinAndQuantity("£2", 1),
                CoinAndQuantity("£1", 1),
                CoinAndQuantity("1p", 1)
            )
        }

        val productAndChange = ProductService(productRepository, changeService).purchaseProduct(
            id = 1,
            coinQuantities = listOf(
                CoinAndQuantity("£2", 2),
                CoinAndQuantity("50p", 2)
            )
        )

        val expectedProduct = Product(
            name = "Plant Based Protein Bar, Caramel",
            price = "£1.99",
            availableQuantity = 9
        )
        val expectedChange = listOf(
            CoinAndQuantity("£2", 1),
            CoinAndQuantity("£1", 1),
            CoinAndQuantity("1p", 1)
        )

        assertEquals(expectedProduct, productAndChange.product)
        assertEquals(expectedChange, productAndChange.change)
    }

    @Test
    fun `purchaseProduct throws an exception if the coin is not accepted`() {
        val productService = ProductService(mock<ProductRepository>(), mock<ChangeService>())

        assertThrows<CoinNotAcceptedException> {
            productService.purchaseProduct(
                id = 1,
                coinQuantities = listOf(
                    CoinAndQuantity("£5", 2),
                    CoinAndQuantity("50p", 2)
                )
            )
        }
    }

    @Test
    fun `purchaseProduct throws an exception if the product does not exist`() {
        val productRepository = mock<ProductRepository> {
            on { findById(1) } doReturn Optional.empty()
        }

        assertThrows<ProductNotFoundException> {
            ProductService(productRepository, mock<ChangeService>()).purchaseProduct(
                id = 1,
                coinQuantities = listOf(
                    CoinAndQuantity("£2", 2),
                    CoinAndQuantity("50p", 2)
                )
            )
        }
    }

    @Test
    fun `purchaseProduct out of stock exception`() {
        val product = Product(
            name = "Plant Based Protein Bar, Caramel",
            price = "£1.99",
            availableQuantity = 0
        )
        val productRepository = mock<ProductRepository> {
            on { findById(1) } doReturn Optional.of(product)
        }

        assertThrows<ProductOutOfStockException> {
            ProductService(productRepository, mock<ChangeService>()).purchaseProduct(
                id = 1,
                coinQuantities = listOf(
                    CoinAndQuantity("£2", 2),
                    CoinAndQuantity("50p", 2)
                )
            )
        }
    }

    @Test
    fun `purchaseProduct throws an exception if not enough money is provided`() {
        val product = Product(
            name = "Plant Based Protein Bar, Caramel",
            price = "£1.99",
            availableQuantity = 10
        )
        val productRepository = mock<ProductRepository> {
            on { findById(1) } doReturn Optional.of(product)
        }

        val productService = ProductService(productRepository, mock<ChangeService>())

        assertThrows<NotEnoughMoneyProvidedException> {
            productService.purchaseProduct(
                id = 1,
                coinQuantities = listOf(
                    CoinAndQuantity("£1", 1)
                )
            )
        }
    }

    @Test
    fun `resetProductQuantities resets the quantities for all products`() {
        val productRepository = mock<ProductRepository> {
            on { findAll() } doReturn listOf(
                Product(
                    name = "Plant Based Protein Bar, Caramel",
                    price = "£1.99",
                    availableQuantity = 0
                ),
                Product(
                    name = "Plant Based Protein Bar, Caramel",
                    price = "£1.99",
                    availableQuantity = 5
                ),
                Product(
                    name = "Plant Based Protein Bar, Caramel",
                    price = "£1.99",
                    availableQuantity = 8
                )
            )
            on { saveAll<Product>(any()) } doAnswer { it.getArgument(0) }
        }

        val products = ProductService(productRepository, mock<ChangeService>()).resetProductQuantities()

        val expectedProducts = listOf(
            Product(
                name = "Plant Based Protein Bar, Caramel",
                price = "£1.99",
                availableQuantity = 10
            ),
            Product(
                name = "Plant Based Protein Bar, Caramel",
                price = "£1.99",
                availableQuantity = 10
            ),
            Product(
                name = "Plant Based Protein Bar, Caramel",
                price = "£1.99",
                availableQuantity = 10
            )
        )

        assertEquals(expectedProducts, products)
    }

}