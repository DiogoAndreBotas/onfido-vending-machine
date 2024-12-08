package diogoandrebotas.onfido.vendingmachine.service

import diogoandrebotas.onfido.vendingmachine.exception.MissingChangeException
import diogoandrebotas.onfido.vendingmachine.exception.NoChangeForNegativeValuesException
import diogoandrebotas.onfido.vendingmachine.model.Change
import diogoandrebotas.onfido.vendingmachine.model.CoinAndQuantity
import diogoandrebotas.onfido.vendingmachine.repository.ChangeRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ChangeService(
    private val changeRepository: ChangeRepository
) {

    fun getChange(): List<Change> = changeRepository.findAll()

    fun resetChange(): List<Change> {
        return changeRepository.saveAll(
            changeRepository.findAll().map {
                when(it.coin) {
                    "£2" -> it.quantity = 5
                    "£1" -> it.quantity = 10
                    "50p" -> it.quantity = 20
                    "20p" -> it.quantity = 50
                    "10p" -> it.quantity = 100
                    "5p" -> it.quantity = 200
                    "2p" -> it.quantity = 500
                    "1p" -> it.quantity = 1000
                }
                it
            }
        )
    }

    fun calculateChange(change: Double): List<CoinAndQuantity> {
        if (change < 0) {
            throw NoChangeForNegativeValuesException()
        } else if (hasEnoughChange(change)) {
            throw MissingChangeException()
        }

        return retrieveChange(change)
    }

    private fun hasEnoughChange(change: Double): Boolean {
        val totalChange = changeRepository.findAll().sumOf {
            if (it.coin.endsWith("p")) {
                it.coin.removeSuffix("p").toDouble().div(100)
            } else {
                it.coin.removePrefix("£").toDouble()
            }.times(it.quantity)
        }

        return totalChange < change
    }

    // BigDecimal is used to limit the result of the division and subtraction to a number with two decimal places
    private fun retrieveChange(change: Double): List<CoinAndQuantity> {
        var missingChange = change
        val changeToReturn = mutableMapOf<String, Int>()

        getCoinsAndMultipliers().forEach { (coin, multiplier) ->
            if (missingChange == 0.0) return@forEach

            val coinsNeeded = (BigDecimal(missingChange.toString()).divide(BigDecimal((1.0 / multiplier).toString()))).toInt()
            if (coinsNeeded == 0)  return@forEach

            val coinQuantity = getAvailableCoins(coinsNeeded, coin)
            val quantityUpdated = coinQuantity.quantity / multiplier

            changeToReturn[coinQuantity.coin] = coinQuantity.quantity
            missingChange = (BigDecimal(missingChange.toString()) - BigDecimal(quantityUpdated.toString())).toDouble()
        }

        return changeToReturn
            .map { CoinAndQuantity(it.key, it.value) }
            .also { updateChangeInDatabase(it) }
    }

    private fun getAvailableCoins(coinsNeeded: Int, coin: String): CoinAndQuantity {
        val change = changeRepository.findById(coin).get()

        if (change.quantity == 0) return CoinAndQuantity(coin, 0)

        val coinQuantity = if ((change.quantity - coinsNeeded) < 0) change.quantity else coinsNeeded

        return CoinAndQuantity(coin, coinQuantity)
    }

    private fun getCoinsAndMultipliers() = listOf(
        Pair("£2", 0.5),
        Pair("£1", 1.0),
        Pair("50p", 2.0),
        Pair("20p", 5.0),
        Pair("10p", 10.0),
        Pair("5p", 20.0),
        Pair("2p", 50.0),
        Pair("1p", 100.0)
    )

    private fun updateChangeInDatabase(coinQuantities: List<CoinAndQuantity>) {
        changeRepository.saveAll(
            coinQuantities.map {
                val change = changeRepository.findById(it.coin).get()
                val currentQuantity = change.quantity
                change.quantity = currentQuantity - it.quantity
                change
            }
        )
    }
}
