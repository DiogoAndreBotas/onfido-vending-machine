package diogoandrebotas.onfido.vendingmachinecli

import diogoandrebotas.onfido.vendingmachinecli.model.CoinAndQuantity
import diogoandrebotas.onfido.vendingmachinecli.model.ProductPurchaseRequestBody
import diogoandrebotas.onfido.vendingmachinecli.model.ProductPurchaseResponseBody
import diogoandrebotas.onfido.vendingmachinecli.model.ProductResponse
import org.http4k.client.ApacheClient
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.format.Moshi
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VendingMachineCLIApp : CommandLineRunner {
	companion object {
		val logger: Logger = LoggerFactory.getLogger(VendingMachineCLIApp::class.java)
		const val BASE_URL = "http://host.docker.internal:8080"
	}

	override fun run(vararg args: String?) {
		logger.info("Booting up the vending machine...")

		logger.info("Are you a customer or an admin?")
		var customerOrAdmin = readln()

		if (customerOrAdmin == "admin") {
			do {
				logger.info("1 - Restock products")
				logger.info("2 - Reset change")
				logger.info("3 - Purchase a product")

				val operation = readln()
				when (operation) {
					"1" -> resetProductQuantities()
					"2" -> resetChange()
					"3" -> customerOrAdmin = "customer"
				}

				var input = ""
				if (customerOrAdmin != "customer") {
					logger.info("Do you wish to continue accessing the machine? (y/n)")
					input = readln()
				}
			} while (input != "n" && customerOrAdmin != "customer")
		}

		if (customerOrAdmin == "customer") {
			do {
				logger.info("These are the available products today...")
				getProducts().sortedBy { it.id }.forEach { logger.info(it.toString()) }

				logger.info("Insert the desired product number:")
				val productId = readln()

				logger.info("Accepted coins: £2, £1, 50p, 20p, 10p, 5p, 2p, 1p")
				logger.info("Insert coins (insert 'p' when you're finished):")
				val coins = mutableListOf<String>()
				while(true) {
					val coin = readln()
					if (coin == "p") {
						break
					} else {
						coins.add(coin)
					}
				}

				val productPurchaseResponseBody = purchaseProduct(
					productId,
					coins.groupingBy { it }.eachCount().map { CoinAndQuantity(it.key, it.value) }
				)
				logger.info("Successfully purchased a ${productPurchaseResponseBody.product.name}!")

				if (productPurchaseResponseBody.change.isNotEmpty()) {
					logger.info("Change: ${
						productPurchaseResponseBody.change.joinToString("; ") {
							"${it.quantity} ${if (it.quantity == 1) "coin" else "coins"} of ${it.coin}"
						}
					}")
				}

				logger.info("Do you wish to purchase any other product? (y/n)")
				val input = readln()
			} while (input != "n")
		}

		if (customerOrAdmin != "customer" && customerOrAdmin != "admin") {
			logger.info("Unsupported role!")
		}

		logger.info("Shutting down the machine...")
	}

	private fun resetProductQuantities() {
		val client: HttpHandler = ApacheClient()
		val request = Request(Method.POST, "$BASE_URL/products/reset")
			.header("Content-Type", "application/json")
		val response = client.invoke(request)

		if (response.status == Status.OK) {
			logger.info("Successfully reset the product quantities!")
		} else {
			logger.info("Error occurred while resetting the product quantities")
		}
	}

	private fun resetChange() {
		val client: HttpHandler = ApacheClient()
		val request = Request(Method.POST, "$BASE_URL/change/reset")
			.header("Content-Type", "application/json")
		val response = client.invoke(request)

		if (response.status == Status.OK) {
			logger.info("Successfully reset the change!")
		} else {
			logger.info("Error occurred while resetting the change")
		}
	}

	private fun getProducts(): List<ProductResponse> {
		val client: HttpHandler = ApacheClient()
		val request = Request(Method.GET, "$BASE_URL/products")
		val response = client.invoke(request)
		val lens = Body.auto<List<ProductResponse>>().toLens()
		val body = lens(response)
		return body
	}

	private fun purchaseProduct(productId: String, coins: List<CoinAndQuantity>): ProductPurchaseResponseBody {
		val client: HttpHandler = ApacheClient()
		val url = "$BASE_URL/products/$productId/purchase"
		val request = Request(Method.POST, url)
			.header("Content-Type", "application/json")
			.body(
				Moshi.asJsonString(
					ProductPurchaseRequestBody(coins),
					ProductPurchaseRequestBody::class
				)
			)
		val response = client.invoke(request)
		val lens = Body.auto<ProductPurchaseResponseBody>().toLens()
		val body = lens(response)
		return body
	}

}

fun main(args: Array<String>) {
	runApplication<VendingMachineCLIApp>(*args)
}
