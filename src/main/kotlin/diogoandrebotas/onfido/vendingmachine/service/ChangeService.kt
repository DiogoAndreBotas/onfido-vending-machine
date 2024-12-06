package diogoandrebotas.onfido.vendingmachine.service

import diogoandrebotas.onfido.vendingmachine.model.Change
import diogoandrebotas.onfido.vendingmachine.model.TempChangeStruct
import diogoandrebotas.onfido.vendingmachine.repository.ChangeRepository
import org.springframework.stereotype.Service
import java.util.*
import kotlin.math.floor

@Service
class ChangeService(
    private val changeRepository: ChangeRepository
) {

    fun getChange(): List<Change> = changeRepository.findAll()

    fun resetChange(): List<Change> {
        val updatedChange = changeRepository.findAll().map {
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

        return changeRepository.saveAll(updatedChange)
    }

    fun calculateChange(value: Float): List<TempChangeStruct> {
        val pounds = floor(value).toInt()
        val pennies = floor((value - pounds) * 100).toInt()

        val changeFromPounds = calculateChangeFromPounds(pounds)
        val changeFromPennies = calculateChangeFromPennies(pennies, changeFromPounds)

        return changeFromPennies.map { TempChangeStruct(it.key, it.value) }
    }

    // TODO: major possibility for refactor, but leave that more towards the end
    private fun calculateChangeFromPounds(pounds: Int): MutableMap<String, Int> {
        var coinsMissing = pounds
        val changeToReturn = mutableMapOf<String, Int>()

        val twoPoundCoinsNeeded = if (coinsMissing % 2 == 0) coinsMissing / 2 else (coinsMissing - 1) / 2
        val twoPoundsPair = getAvailableCoins(twoPoundCoinsNeeded, "£2", 0.5F)
        changeToReturn[twoPoundsPair.get().first.coin] = twoPoundsPair.get().first.quantity
        coinsMissing -= twoPoundsPair.get().second

        val onePoundPair = getAvailableCoins(coinsMissing, "£1", 1F)
        changeToReturn[onePoundPair.get().first.coin] = onePoundPair.get().first.quantity
        coinsMissing -= onePoundPair.get().second

        val fiftyPCoinsNeeded = (coinsMissing / 0.5).toInt()
        val fiftyPPair = getAvailableCoins(fiftyPCoinsNeeded, "50p", 2F)
        changeToReturn[fiftyPPair.get().first.coin] = fiftyPPair.get().first.quantity
        coinsMissing -= fiftyPPair.get().second

        val twentyPCoinsNeeded = (coinsMissing / 0.2).toInt()
        val twentyPPair = getAvailableCoins(twentyPCoinsNeeded, "20p", 5F)
        changeToReturn[twentyPPair.get().first.coin] = twentyPPair.get().first.quantity
        coinsMissing -= twentyPPair.get().second

        val tenPCoinsNeeded = (coinsMissing / 0.1).toInt()
        val tenPPair = getAvailableCoins(tenPCoinsNeeded, "10p", 10F)
        changeToReturn[tenPPair.get().first.coin] = tenPPair.get().first.quantity
        coinsMissing -= tenPPair.get().second

        val fivePCoinsNeeded = (coinsMissing / 0.05).toInt()
        val fivePPair = getAvailableCoins(fivePCoinsNeeded, "5p", 20F)
        changeToReturn[fivePPair.get().first.coin] = fivePPair.get().first.quantity
        coinsMissing -= fivePPair.get().second

        val twoPCoinsNeeded = (coinsMissing / 0.02).toInt()
        val twoPPair = getAvailableCoins(twoPCoinsNeeded, "2p", 50F)
        changeToReturn[twoPPair.get().first.coin] = twoPPair.get().first.quantity
        coinsMissing -= twoPPair.get().second

        val onePCoinsNeeded = (coinsMissing / 0.01).toInt()
        val onePPair = getAvailableCoins(onePCoinsNeeded, "1p", 100F)
        changeToReturn[onePPair.get().first.coin] = onePPair.get().first.quantity
        coinsMissing -= onePPair.get().second

        if (coinsMissing > 0) {
            throw Exception("Not enough change")
        }

        return changeToReturn
    }

    private fun calculateChangeFromPennies(pennies: Int, currentChange: MutableMap<String, Int>): Map<String, Int> {
        var coinsMissing = pennies

        val fiftyPCoinsNeeded = coinsMissing / 50
        val fiftyPPair = getAvailableCoins(fiftyPCoinsNeeded, "50p", 0.02F)
        val currFiftyCoins = currentChange["50p"]
        currentChange["50p"] = (currFiftyCoins ?: 0) + fiftyPPair.get().first.quantity
        coinsMissing -= fiftyPPair.get().second

        val twentyPCoinsNeeded = coinsMissing / 20
        val twentyPPair = getAvailableCoins(twentyPCoinsNeeded, "20p", 0.05F)
        val currTwentyCoins = currentChange["20p"]
        currentChange["20p"] = (currTwentyCoins ?: 0) + twentyPPair.get().first.quantity
        coinsMissing -= twentyPPair.get().second

        val tenPCoinsNeeded = (coinsMissing / 10)
        val tenPPair = getAvailableCoins(tenPCoinsNeeded, "10p", 0.1F)
        val currTenCoins = currentChange["10p"]
        currentChange["10p"] = (currTenCoins ?: 0) + tenPPair.get().first.quantity
        coinsMissing -= tenPPair.get().second

        val fivePCoinsNeeded = (coinsMissing / 5)
        val fivePPair = getAvailableCoins(fivePCoinsNeeded, "5p", 0.2F)
        val currFiveCoins = currentChange["5p"]
        currentChange["5p"] = (currFiveCoins ?: 0) + fivePPair.get().first.quantity
        coinsMissing -= fivePPair.get().second

        val twoPCoinsNeeded = (coinsMissing / 2)
        val twoPPair = getAvailableCoins(twoPCoinsNeeded, "2p", 0.5F)
        val currTwoCoins = currentChange["2p"]
        currentChange["2p"] = (currTwoCoins ?: 0) + twoPPair.get().first.quantity
        coinsMissing -= twoPPair.get().second

        val onePCoinsNeeded = (coinsMissing / 1)
        val onePPair = getAvailableCoins(onePCoinsNeeded, "1p", 1F)
        val currOneCoins = currentChange["1p"]
        currentChange["1p"] = (currOneCoins ?: 0) + onePPair.get().first.quantity
        coinsMissing -= onePPair.get().second

        if (coinsMissing > 0) {
            throw Exception("Not enough change")
        }

        return currentChange.filter { it.value != 0 }
    }

    private fun getAvailableCoins(coinsNeeded: Int, coin: String, divideBy: Float): Optional<Pair<TempChangeStruct, Int>> {
        val change = changeRepository.findById(coin).get()
        if (coinsNeeded > 0 && change.quantity > 0) {
            val quantity = if ((change.quantity - coinsNeeded) < 0) {
                change.quantity
            } else {
                coinsNeeded
            }

            // TODO: refactor -> move into private method for better readability
            change.quantity -= quantity
            changeRepository.save(change)

            val quantityUpdated = (quantity / divideBy).toInt()
            return Optional.of(Pair(TempChangeStruct(coin, quantity), quantityUpdated))
        } else {
            return Optional.of(Pair(TempChangeStruct(coin, 0), 0))
        }
    }

}
