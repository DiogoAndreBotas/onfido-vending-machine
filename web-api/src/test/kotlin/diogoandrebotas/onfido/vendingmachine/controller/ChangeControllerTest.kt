package diogoandrebotas.onfido.vendingmachine.controller

import diogoandrebotas.onfido.vendingmachine.model.Change
import diogoandrebotas.onfido.vendingmachine.service.ChangeService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class ChangeControllerTest {

    @Test
    fun `getChange returns change`() {
        val changeService = mock<ChangeService> {
            on { getChange() } doReturn listOf(
                Change("£2", 5),
                Change("£1", 10)
            )
        }

        val change = ChangeController(changeService).getChange()

        val expectedChange = listOf(
            Change("£2", 5),
            Change("£1", 10)
        )

        assertEquals(expectedChange, change)
    }

    @Test
    fun `resetChange resets change`() {
        val changeService = mock<ChangeService> {
            on { resetChange() } doReturn listOf(
                Change("£2", 5),
                Change("£1", 10)
            )
        }

        val change = ChangeController(changeService).resetChange()

        val expectedChange = listOf(
            Change("£2", 5),
            Change("£1", 10)
        )

        assertEquals(expectedChange, change)
    }

}