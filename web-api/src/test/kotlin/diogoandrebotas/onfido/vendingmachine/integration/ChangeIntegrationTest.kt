package diogoandrebotas.onfido.vendingmachine.integration

import diogoandrebotas.onfido.vendingmachine.model.Change
import diogoandrebotas.onfido.vendingmachine.repository.ChangeRepository
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
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
class ChangeIntegrationTest {
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
    private lateinit var changeRepository: ChangeRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @Order(1)
    fun `getChange endpoint returns the change available`() {
        val response = mockMvc.perform(get("/change"))
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        val change = Json.decodeFromString<List<Change>>(response)

        val expectedChange = change()

        assertEquals(expectedChange, change)
    }

    @Test
    @Order(2)
    fun `resetChange endpoint resets the change`() {
        changeRepository.saveAll(
            changeRepository.findAll().map {
                it.quantity = 0
                it
            }
        )

        val response = mockMvc.perform(post("/change/reset"))
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        val change = Json.decodeFromString<List<Change>>(response)

        val expectedChange = change()

        assertEquals(expectedChange, change)
    }

    private fun change(): List<Change> {
        return listOf(
            Change("£2", 5),
            Change("£1", 10),
            Change("50p", 20),
            Change("20p", 50),
            Change("10p", 100),
            Change("5p", 200),
            Change("2p", 500),
            Change("1p", 1000),
        )
    }

}