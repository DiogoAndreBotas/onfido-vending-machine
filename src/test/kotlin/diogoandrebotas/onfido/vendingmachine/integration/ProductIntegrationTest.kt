package diogoandrebotas.onfido.vendingmachine.integration

import diogoandrebotas.onfido.vendingmachine.model.CoinQuantity
import diogoandrebotas.onfido.vendingmachine.model.Product
import diogoandrebotas.onfido.vendingmachine.model.http.ErrorResponseBody
import diogoandrebotas.onfido.vendingmachine.model.http.ProductPurchaseResponseBody
import diogoandrebotas.onfido.vendingmachine.model.http.PurchaseRequestBody
import diogoandrebotas.onfido.vendingmachine.repository.ProductRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ProductIntegrationTest {
    companion object {
        val database = PostgreSQLContainer("postgres:16-alpine")

        @BeforeAll
        @JvmStatic
        fun startDBContainer() {
            database.start()
        }

        @AfterAll
        @JvmStatic
        fun stopDBContainer() {
            database.stop()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerDBContainer(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", database::getJdbcUrl)
            registry.add("spring.datasource.username", database::getUsername)
            registry.add("spring.datasource.password", database::getPassword)
        }
    }

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @Order(1)
    fun `getProducts endpoint returns the products`() {
        val response = mockMvc.perform(get("/products"))
            .andExpect(status().isOk)
            .andReturn().response.contentAsString
        val products = Json.decodeFromString<List<Product>>(response)

        val expectedProducts = products()

        assertEquals(expectedProducts, products)
    }

    @Test
    @Order(2)
    fun `getProduct endpoint returns the product`() {
        val response = mockMvc.perform(get("/products/1"))
            .andExpect(status().isOk)
            .andReturn().response.contentAsString
        val product = Json.decodeFromString<Product>(response)

        val expectedProduct = Product(
            id = 1,
            name = "Plant Based Protein Bar, Caramel",
            price = "£1.99",
            availableQuantity = 10
        )

        assertEquals(expectedProduct, product)
    }

    @Test
    @Order(3)
    fun `getProduct endpoint returns an error if the product does not exist`() {
        val response = mockMvc.perform(get("/products/100"))
            .andExpect(status().isNotFound)
            .andReturn().response.contentAsString
        val errorResponse = Json.decodeFromString<ErrorResponseBody>(response)

        val expectedErrorResponse = ErrorResponseBody(
            HttpStatus.NOT_FOUND.value(),
            "The product with the ID 100 is not available"
        )

        assertEquals(expectedErrorResponse, errorResponse)
    }

    @Test
    @Order(4)
    fun `purchaseProduct endpoint returns the product and change if the change is in pounds`() {
        val response = mockMvc.perform(
                post("/products/3/purchase")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        Json.encodeToString(
                            value = PurchaseRequestBody(
                                listOf(CoinQuantity("£2", 4))
                            )
                        )
                    )
            )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        val responseBody = Json.decodeFromString<ProductPurchaseResponseBody>(response)

        val expectedResponseBody = ProductPurchaseResponseBody(
            Product(
                id = 3,
                name = "Coke Zero, Lime",
                price = "£2",
                availableQuantity = 9
            ),
            listOf(CoinQuantity("£2", 3))
        )

        assertEquals(expectedResponseBody, responseBody)
    }

    @Test
    @Order(5)
    fun `purchaseProduct endpoint returns the product and change if the change is in pennies`() {
        val response = mockMvc.perform(
            post("/products/4/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    Json.encodeToString(
                        value = PurchaseRequestBody(
                            listOf(CoinQuantity("£1", 1))
                        )
                    )
                )
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        val responseBody = Json.decodeFromString<ProductPurchaseResponseBody>(response)

        val expectedResponseBody = ProductPurchaseResponseBody(
            Product(
                id = 4,
                name = "Fanta Zero, Orange",
                price = "99p",
                availableQuantity = 9
            ),
            listOf(CoinQuantity("1p", 1))
        )

        assertEquals(expectedResponseBody, responseBody)
    }

    @Test
    @Order(6)
    fun `purchaseProduct endpoint returns the product and change if the change is in pounds and pennies`() {
        val response = mockMvc.perform(
            post("/products/2/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    Json.encodeToString(
                        value = PurchaseRequestBody(
                            listOf(CoinQuantity("£2", 1))
                        )
                    )
                )
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        val responseBody = Json.decodeFromString<ProductPurchaseResponseBody>(response)

        val expectedResponseBody = ProductPurchaseResponseBody(
            Product(
                id = 2,
                name = "Plant Based Protein Bar, Strawberry",
                price = "£1.85",
                availableQuantity = 9
            ),
            listOf(
                CoinQuantity("10p", 1),
                CoinQuantity("5p", 1)
            )
        )

        assertEquals(expectedResponseBody, responseBody)
    }

    @Test
    @Order(7)
    fun `purchaseProduct endpoint returns the product and change if the change is zero`() {
        val response = mockMvc.perform(
            post("/products/2/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    Json.encodeToString(
                        value = PurchaseRequestBody(
                            listOf(
                                CoinQuantity("£1", 1),
                                CoinQuantity("50p", 1),
                                CoinQuantity("20p", 1),
                                CoinQuantity("10p", 1),
                                CoinQuantity("5p", 1),
                            )
                        )
                    )
                )
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        val responseBody = Json.decodeFromString<ProductPurchaseResponseBody>(response)

        val expectedResponseBody = ProductPurchaseResponseBody(
            Product(
                id = 2,
                name = "Plant Based Protein Bar, Strawberry",
                price = "£1.85",
                availableQuantity = 8
            ),
            emptyList()
        )

        assertEquals(expectedResponseBody, responseBody)
    }

    @Test
    @Order(8)
    fun `purchaseProduct endpoint returns an error if there isn't enough change`() {
        val response = mockMvc.perform(
                post("/products/1/purchase")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        Json.encodeToString(
                            value = PurchaseRequestBody(
                                listOf(CoinQuantity("£2", 500))
                            )
                        )
                    )
            )
            .andExpect(status().isConflict)
            .andReturn().response.contentAsString

        val errorResponse = Json.decodeFromString<ErrorResponseBody>(response)

        val expectedErrorResponse = ErrorResponseBody(
            HttpStatus.CONFLICT.value(),
            "The vending machine doesn't have enough change"
        )

        assertEquals(expectedErrorResponse, errorResponse)
    }

    @Test
    @Order(9)
    fun `purchaseProduct endpoint returns an error if the coin is not accepted`() {
        val response = mockMvc.perform(
            post("/products/1/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    Json.encodeToString(
                        value = PurchaseRequestBody(
                            listOf(CoinQuantity("£5", 1))
                        )
                    )
                )
        )
            .andExpect(status().isBadRequest)
            .andReturn().response.contentAsString

        val errorResponse = Json.decodeFromString<ErrorResponseBody>(response)

        val expectedErrorResponse = ErrorResponseBody(
            HttpStatus.BAD_REQUEST.value(),
            "The coin £5 is not accepted"
        )

        assertEquals(expectedErrorResponse, errorResponse)
    }

    @Test
    @Order(10)
    fun `purchaseProduct endpoint returns an error if not enough money is provided`() {
        val response = mockMvc.perform(
            post("/products/1/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    Json.encodeToString(
                        value = PurchaseRequestBody(
                            listOf(CoinQuantity("1p", 1))
                        )
                    )
                )
        )
            .andExpect(status().isBadRequest)
            .andReturn().response.contentAsString

        val errorResponse = Json.decodeFromString<ErrorResponseBody>(response)

        val expectedErrorResponse = ErrorResponseBody(
            HttpStatus.BAD_REQUEST.value(),
            "The product costs £1.99 and you provided only 1p"
        )

        assertEquals(expectedErrorResponse, errorResponse)
    }

    @Test
    @Order(11)
    fun `purchaseProduct endpoint returns an error if the product is out of stock`() {
        val product = Product(
            name = "Ruffles, Potato Chips, Classic",
            price = "£2.5",
            availableQuantity = 0
        )
        productRepository.save(product)

        val response = mockMvc.perform(
            post("/products/${product.id}/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    Json.encodeToString(
                        value = PurchaseRequestBody(
                            listOf(CoinQuantity("£2", 5))
                        )
                    )
                )
        )
            .andExpect(status().isConflict)
            .andReturn().response.contentAsString

        val errorResponse = Json.decodeFromString<ErrorResponseBody>(response)

        val expectedErrorResponse = ErrorResponseBody(
            HttpStatus.CONFLICT.value(),
            "The product ${product.name} is out of stock"
        )

        assertEquals(expectedErrorResponse, errorResponse)

        productRepository.deleteById(product.id)
    }

    @Test
    @Order(12)
    fun `resetProductQuantities endpoint resets the product quantities`() {
        productRepository.saveAll(
            productRepository.findAll().map {
                it.availableQuantity = 0
                it
            }
        )

        val response = mockMvc.perform(post("/products/reset"))
            .andExpect(status().isOk)
            .andReturn().response.contentAsString
        val products = Json.decodeFromString<List<Product>>(response)

        products.forEach {
            assertEquals(10, it.availableQuantity)
        }
    }

    private fun products(): List<Product> {
        return listOf(
            Product(
                id = 1,
                name = "Plant Based Protein Bar, Caramel",
                price = "£1.99",
                availableQuantity = 10
            ),
            Product(
                id = 2,
                name = "Plant Based Protein Bar, Strawberry",
                price = "£1.85",
                availableQuantity = 10
            ),
            Product(
                id = 3,
                name = "Coke Zero, Lime",
                price = "£2",
                availableQuantity = 10
            ),
            Product(
                id = 4,
                name = "Fanta Zero, Orange",
                price = "99p",
                availableQuantity = 10
            ),
            Product(
                id = 5,
                name = "Lay's, Potato Chips, Classic",
                price = "£2.5",
                availableQuantity = 10
            ),
        )
    }
}