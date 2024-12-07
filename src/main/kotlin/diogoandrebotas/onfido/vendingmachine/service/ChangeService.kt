package diogoandrebotas.onfido.vendingmachine.service

import diogoandrebotas.onfido.vendingmachine.exception.MissingChangeException
import diogoandrebotas.onfido.vendingmachine.exception.NoChangeForNegativeValuesException
import diogoandrebotas.onfido.vendingmachine.model.Change
import diogoandrebotas.onfido.vendingmachine.model.CoinQuantity
import diogoandrebotas.onfido.vendingmachine.repository.ChangeRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ChangeService(
    private val changeRepository: ChangeRepository
) {

    fun getChange(): List<Change> = changeRepository.findAll()

    fun resetChange(): List<Change> {
        val changeToUpdate = changeRepository.findAll()

        changeToUpdate.forEach {
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
        }

        return changeRepository.saveAll(changeToUpdate)
    }

    fun calculateChange(value: Double): List<CoinQuantity> {
        if (value < 0) {
            throw NoChangeForNegativeValuesException()
        } else if (hasEnoughChange(value)) {
            throw MissingChangeException()
        }

        return retrieveCoins(value, getCoinsAndMultipliers()).map { CoinQuantity(it.key, it.value) }
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

    private fun retrieveCoins(value: Double, coinsToConsider: List<Pair<String, Double>>): MutableMap<String, Int> {
        var coinsMissing = value
        val changeToReturn = mutableMapOf<String, Int>()

        coinsToConsider.forEach { (coin, multiplier) ->
            if (coinsMissing == 0.0) return@forEach

            val coinsNeeded = (BigDecimal(coinsMissing.toString()).divide(BigDecimal((1.0 / multiplier).toString()))).toInt()
            if (coinsNeeded == 0)  return@forEach

            val coinQuantity = getAvailableCoins(coinsNeeded, coin)
            val quantityUpdated = coinQuantity.quantity / multiplier

            changeToReturn[coinQuantity.coin] = coinQuantity.quantity
            coinsMissing = (BigDecimal(coinsMissing.toString()) - BigDecimal(quantityUpdated.toString())).toDouble()
        }

        removeChange(changeToReturn)

        return changeToReturn
    }

    private fun getAvailableCoins(coinsNeeded: Int, coin: String): CoinQuantity {
        val change = changeRepository.findById(coin).get()

        if (change.quantity == 0) return CoinQuantity(coin, 0)

        val quantity = if ((change.quantity - coinsNeeded) < 0) {
            change.quantity
        } else {
            coinsNeeded
        }

        return CoinQuantity(coin, quantity)
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

    private fun removeChange(coinQuantities: Map<String, Int>) {
        changeRepository.saveAll(
            coinQuantities.map {
                val change = changeRepository.findById(it.key).get()
                val currentQuantity = change.quantity
                change.quantity = currentQuantity - it.value
                change
            }
        )
    }

}
