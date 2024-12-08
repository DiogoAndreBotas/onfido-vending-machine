package diogoandrebotas.onfido.vendingmachine.service

import diogoandrebotas.onfido.vendingmachine.exception.MissingChangeException
import diogoandrebotas.onfido.vendingmachine.exception.NoChangeForNegativeValuesException
import diogoandrebotas.onfido.vendingmachine.model.Change
import diogoandrebotas.onfido.vendingmachine.model.CoinAndQuantity
import diogoandrebotas.onfido.vendingmachine.repository.ChangeRepository
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
class ChangeServiceTest {

    @Test
    fun `getChange returns change`() {
        val changeRepository = mock<ChangeRepository> {
            on { findAll() } doReturn listOf(
                Change("£2", 5),
                Change("£1", 10)
            )
        }

        val change = ChangeService(changeRepository).getChange()

        val expectedChange = listOf(
            Change("£2", 5),
            Change("£1", 10)
        )

        assertEquals(expectedChange, change)
    }

    @Test
    fun `resetChange resets the change`() {
        val changeRepository = mock<ChangeRepository> {
            on { findAll() } doReturn emptyChange()
            on { saveAll<Change>(any()) } doAnswer { it.getArgument(0) }
        }

        val change = ChangeService(changeRepository).resetChange()

        val expectedChange = change()

        assertEquals(expectedChange, change)
    }

    @Test
    fun `calculateChange calculates the change for pounds`() {
        val changeRepository = mock<ChangeRepository> {
            on { findById("£2") } doReturn Optional.of(Change("£2", 1))
            on { findById("£1") } doReturn Optional.of(Change("£1", 2))
            on { findById("50p") } doReturn Optional.of(Change("50p", 2))
            on { findById("20p") } doReturn Optional.of(Change("20p", 5))
            on { findById("10p") } doReturn Optional.of(Change("10p", 10))
            on { findById("5p") } doReturn Optional.of(Change("5p", 20))
            on { findById("2p") } doReturn Optional.of(Change("2p", 50))
            on { findById("1p") } doReturn Optional.of(Change("1p", 100))
            on { findAll() } doReturn listOf(
                Change("£2", 1),
                Change("£1", 2),
                Change("50p", 2),
                Change("20p", 5),
                Change("10p", 10),
                Change("5p", 20),
                Change("2p", 50),
                Change("1p", 100),
            )
        }

        val change = ChangeService(changeRepository).calculateChange(10.00)

        val expectedChange = listOf(
            CoinAndQuantity("£2", 1),
            CoinAndQuantity("£1", 2),
            CoinAndQuantity("50p", 2),
            CoinAndQuantity("20p", 5),
            CoinAndQuantity("10p", 10),
            CoinAndQuantity("5p", 20),
            CoinAndQuantity("2p", 50),
            CoinAndQuantity("1p", 100),
        )

        assertEquals(expectedChange, change)
    }

    @Test
    fun `calculateChange calculates the change for pennies`() {
        val changeRepository = mock<ChangeRepository> {
            on { findById("50p") } doReturn Optional.of(Change("50p", 1))
            on { findById("20p") } doReturn Optional.of(Change("20p", 2))
            on { findById("5p") } doReturn Optional.of(Change("5p", 20))
            on { findById("2p") } doReturn Optional.of(Change("2p", 50))
            on { findAll() } doReturn listOf(
                Change("50p", 1),
                Change("20p", 2),
                Change("5p", 20),
                Change("2p", 50),
            )
        }

        val change = ChangeService(changeRepository).calculateChange(0.99)

        val expectedChange = listOf(
            CoinAndQuantity("50p", 1),
            CoinAndQuantity("20p", 2),
            CoinAndQuantity("5p", 1),
            CoinAndQuantity("2p", 2)
        )

        assertEquals(expectedChange, change)
    }

    @Test
    fun `calculateChange calculates the change for pounds and pennies`() {
        val changeRepository = mock<ChangeRepository> {
            on { findById("£2") } doReturn Optional.of(Change("£2", 45))
            on { findById("£1") } doReturn Optional.of(Change("£1", 4))
            on { findById("50p") } doReturn Optional.of(Change("50p", 2))
            on { findById("20p") } doReturn Optional.of(Change("20p", 5))
            on { findById("10p") } doReturn Optional.of(Change("10p", 10))
            on { findById("5p") } doReturn Optional.of(Change("5p", 20))
            on { findById("2p") } doReturn Optional.of(Change("2p", 50))
            on { findById("1p") } doReturn Optional.of(Change("1p", 85))
            on { findAll() } doReturn listOf(
                Change("£2", 45),
                Change("£1", 4),
                Change("50p", 2),
                Change("20p", 5),
                Change("10p", 10),
                Change("5p", 20),
                Change("2p", 50),
                Change("1p", 85),
            )
        }

        val change = ChangeService(changeRepository).calculateChange(99.85)

        val expectedChange = listOf(
            CoinAndQuantity("£2", 45),
            CoinAndQuantity("£1", 4),
            CoinAndQuantity("50p", 2),
            CoinAndQuantity("20p", 5),
            CoinAndQuantity("10p", 10),
            CoinAndQuantity("5p", 20),
            CoinAndQuantity("2p", 50),
            CoinAndQuantity("1p", 85),
        )

        assertEquals(expectedChange, change)
    }

    @Test
    fun `calculateChange calculates the change for zero`() {
        val change = ChangeService(mock<ChangeRepository>()).calculateChange(0.00)

        val expectedChange = emptyList<CoinAndQuantity>()

        assertEquals(expectedChange, change)
    }

    @Test
    fun `calculateChange for negative infinity`() {
        val changeService = ChangeService(mock<ChangeRepository>())

        assertThrows<NoChangeForNegativeValuesException> { changeService.calculateChange(Double.NEGATIVE_INFINITY) }
    }

    @Test
    fun `calculateChange for positive infinity`() {
        val changeService = ChangeService(mock<ChangeRepository>())

        assertThrows<MissingChangeException> { changeService.calculateChange(Double.POSITIVE_INFINITY) }
    }

    @Test
    fun `calculateChange throws an exception if there isn't enough change`() {
        val changeService = ChangeService(mock<ChangeRepository>())

        assertThrows<MissingChangeException> { changeService.calculateChange(100.00) }
    }

    private fun emptyChange(): List<Change> {
        return listOf(
            Change("£2", 0),
            Change("£1", 0),
            Change("50p", 0),
            Change("20p", 0),
            Change("10p", 0),
            Change("5p", 0),
            Change("2p", 0),
            Change("1p", 0),
        )
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