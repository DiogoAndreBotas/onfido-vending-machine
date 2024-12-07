package diogoandrebotas.onfido.vendingmachine.service

import diogoandrebotas.onfido.vendingmachine.exception.MissingChangeException
import diogoandrebotas.onfido.vendingmachine.exception.NoChangeForNegativeValuesException
import diogoandrebotas.onfido.vendingmachine.model.Change
import diogoandrebotas.onfido.vendingmachine.model.CoinQuantity
import diogoandrebotas.onfido.vendingmachine.repository.ChangeRepository
import org.springframework.stereotype.Service
import kotlin.math.floor
import kotlin.math.round

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
        val pounds = floor(value).toInt()
        val pennies = round((value - pounds) * 100).toInt()

        if (pounds < 0 || pennies < 0) {
            throw NoChangeForNegativeValuesException()
        } else if (hasEnoughChange(value)) {
            throw MissingChangeException()
        }

        val changeFromPounds = retrieveCoins(pounds, getCoinsAndMultipliersForPounds())
        val changeFromPennies = retrieveCoins(pennies, getCoinsAndMultipliersForPennies())

        return mergeCoinQuantityLists(changeFromPounds, changeFromPennies)
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

    private fun retrieveCoins(value: Int, coinsToConsider: List<Pair<String, Float>>): MutableMap<String, Int> {
        var coinsMissing = value
        val changeToReturn = mutableMapOf<String, Int>()

        coinsToConsider.forEach { (coin, multiplier) ->
            if (coinsMissing == 0) return@forEach

            val coinsNeeded = (coinsMissing / (1 / multiplier)).toInt()
            if (coinsNeeded == 0)  return@forEach

            val coinQuantity = getAvailableCoins(coinsNeeded, coin)
            val quantityUpdated = (coinQuantity.quantity / multiplier).toInt()

            changeToReturn[coinQuantity.coin] = coinQuantity.quantity
            coinsMissing -= quantityUpdated
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

    private fun getCoinsAndMultipliersForPounds() = mutableListOf(
        Pair("£2", 0.5F),
        Pair("£1", 1F),
        Pair("50p", 2F),
        Pair("20p", 5F),
        Pair("10p", 10F),
        Pair("5p", 20F),
        Pair("2p", 50F),
        Pair("1p", 100F)
    )

    private fun getCoinsAndMultipliersForPennies() = mutableListOf(
        Pair("50p", 0.02F),
        Pair("20p", 0.05F),
        Pair("10p", 0.1F),
        Pair("5p", 0.2F),
        Pair("2p", 0.5F),
        Pair("1p", 1F)
    )

    private fun mergeCoinQuantityLists(changeFromPounds: Map<String, Int>, changeFromPennies: Map<String, Int>): List<CoinQuantity> {
        return (changeFromPounds.toList() + changeFromPennies.toList())
            .groupBy({ it.first }, { it.second })
            .map { (key, values) -> key to values.sum() }
            .filter { it.second != 0 }
            .map { CoinQuantity(it.first, it.second) }
    }

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
