package diogoandrebotas.onfido.vendingmachine.controller

import diogoandrebotas.onfido.vendingmachine.model.CoinAndQuantity
import diogoandrebotas.onfido.vendingmachine.model.Product
import diogoandrebotas.onfido.vendingmachine.model.ProductAndChange
import diogoandrebotas.onfido.vendingmachine.model.http.ProductPurchaseRequestBody
import diogoandrebotas.onfido.vendingmachine.service.ProductService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class ProductControllerTest {

    @Test
    fun `getProducts returns products`() {
        val productService = mock<ProductService> {
            on { getProducts() } doReturn listOf(
                Product(
                    name = "Plant Based Protein Bar, Caramel",
                    price = "£1.99",
                    availableQuantity = 10
                )
            )
        }

        val products = ProductController(productService).getProducts()

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
        val productService = mock<ProductService> {
            on { getProduct(1) } doReturn Product(
                name = "Plant Based Protein Bar, Caramel",
                price = "£1.99",
                availableQuantity = 10
            )
        }

        val product = ProductController(productService).getProduct(1)

        val expectedProduct = Product(
            name = "Plant Based Protein Bar, Caramel",
            price = "£1.99",
            availableQuantity = 10
        )

        assertEquals(expectedProduct, product)
    }

    @Test
    fun `purchaseProduct returns the product and the change`() {
        val coinQuantities = listOf(CoinAndQuantity("£2", 2), CoinAndQuantity("50p", 1))
        val productService = mock<ProductService> {
            on { purchaseProduct(1, coinQuantities) } doReturn ProductAndChange(
                Product(
                    name = "Plant Based Protein Bar, Caramel",
                    price = "£2",
                    availableQuantity = 10
                ),
                listOf(
                    CoinAndQuantity("£2", 1),
                    CoinAndQuantity("50p", 1)
                )
            )
        }

        val result = ProductController(productService).purchaseProduct(1, ProductPurchaseRequestBody(coinQuantities))

        val expectedResult = Pair(
            Product(
                name = "Plant Based Protein Bar, Caramel",
                price = "£2",
                availableQuantity = 10
            ),
            listOf(
                CoinAndQuantity("£2", 1),
                CoinAndQuantity("50p", 1)
            )
        )

        assertEquals(expectedResult.first, result.product)
        assertEquals(expectedResult.second, result.change)
    }

    @Test
    fun `resetProductQuantities resets the product quantities`() {
        val productService = mock<ProductService> {
            on { resetProductQuantities() } doReturn listOf(
                Product(
                    name = "Plant Based Protein Bar, Caramel",
                    price = "£1.99",
                    availableQuantity = 10
                )
            )
        }

        val products = ProductController(productService).resetProductQuantities()

        val expectedProducts = listOf(
            Product(
                name = "Plant Based Protein Bar, Caramel",
                price = "£1.99",
                availableQuantity = 10
            )
        )

        assertEquals(expectedProducts, products)
    }

}