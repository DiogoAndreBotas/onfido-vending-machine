package diogoandrebotas.onfido.vendingmachine.service

import diogoandrebotas.onfido.vendingmachine.exception.MissingChangeException
import diogoandrebotas.onfido.vendingmachine.exception.NoChangeForNegativeValuesException
import diogoandrebotas.onfido.vendingmachine.model.Change
import diogoandrebotas.onfido.vendingmachine.model.CoinQuantity
import diogoandrebotas.onfido.vendingmachine.repository.ChangeRepository
import org.springframework.stereotype.Service
import java.util.*
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
        }

        val changeFromPounds = calculateChangeFromPounds(pounds)
        val changeFromPennies = calculateChangeFromPennies(pennies, changeFromPounds)

        return changeFromPennies.map { CoinQuantity(it.key, it.value) }
    }

    // TODO: major possibility for refactor, join pounds and pennies logic together
    private fun calculateChangeFromPounds(pounds: Int): MutableMap<String, Int> {
        var coinsMissing = pounds
        val changeToReturn = mutableMapOf<String, Int>()

        listOf(
            Pair("£2", 0.5F),
            Pair("£1", 1F),
            Pair("50p", 2F),
            Pair("20p", 5F),
            Pair("10p", 10F),
            Pair("5p", 20F),
            Pair("2p", 50F),
            Pair("1p", 100F)
        ).forEach { (coin, multiplier) ->
            if (coinsMissing == 0) return@forEach

            val coinsNeeded = (coinsMissing / (1 / multiplier)).toInt()
            if (coinsNeeded == 0)  return@forEach

            val coinQuantityAndQuantityUpdated = getAvailableCoins(coinsNeeded, coin, multiplier)
            changeToReturn[coinQuantityAndQuantityUpdated.get().first.coin] = coinQuantityAndQuantityUpdated.get().first.quantity
            coinsMissing -= coinQuantityAndQuantityUpdated.get().second
        }

        if (coinsMissing > 0) throw MissingChangeException()

        return changeToReturn
    }

    private fun calculateChangeFromPennies(pennies: Int, currentChange: MutableMap<String, Int>): Map<String, Int> {
        var coinsMissing = pennies

        listOf(
            Pair("50p", 0.02F),
            Pair("20p", 0.05F),
            Pair("10p", 0.1F),
            Pair("5p", 0.2F),
            Pair("2p", 0.5F),
            Pair("1p", 1F)
            // TODO: find other places where I loop through pairs and this convention can be used
        ).forEach { (coin, multiplier) ->
            if (coinsMissing == 0) return@forEach

            val coinsNeeded = (coinsMissing / (1 / multiplier)).toInt()
            if (coinsNeeded == 0)  return@forEach

            val coinQuantityAndQuantityUpdated = getAvailableCoins(coinsNeeded, coin, multiplier)
            currentChange[coin] = (currentChange[coin] ?: 0) + coinQuantityAndQuantityUpdated.get().first.quantity
            coinsMissing -= coinQuantityAndQuantityUpdated.get().second
        }

        if (coinsMissing > 0) throw MissingChangeException()

        // TODO: find a way to not need this filter
        return currentChange.filter { it.value != 0 }
    }

    private fun getAvailableCoins(coinsNeeded: Int, coin: String, divideBy: Float): Optional<Pair<CoinQuantity, Int>> {
        // TODO: consider loading coins into an hashmap for faster access
        val change = changeRepository.findById(coin).get()

        if (coinsNeeded > 0 && change.quantity > 0) {
            val quantity = if ((change.quantity - coinsNeeded) < 0) {
                change.quantity
            } else {
                coinsNeeded
            }

            // TODO: find a way to roll this back, when error is thrown changeRepository is updated
            // TODO: update a list with change to be updated, and only update after exception is thrown
            decreaseChangeQuantity(change, quantity)

            // TODO: investigate how to remove Pair
            val quantityUpdated = (quantity / divideBy).toInt()
            return Optional.of(Pair(CoinQuantity(coin, quantity), quantityUpdated))
        } else {
            // TODO: address this when refactoring code on top
            return Optional.of(Pair(CoinQuantity(coin, 0), 0))
        }
    }

    private fun decreaseChangeQuantity(change: Change, quantity: Int) {
        change.quantity -= quantity
        changeRepository.save(change)
    }

}
