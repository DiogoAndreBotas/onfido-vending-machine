package diogoandrebotas.onfido.vendingmachine.controller

import diogoandrebotas.onfido.vendingmachine.model.http.ProductPurchaseResponseBody
import diogoandrebotas.onfido.vendingmachine.model.http.PurchaseRequestBody
import diogoandrebotas.onfido.vendingmachine.service.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductController(
    private val productService: ProductService
) {

    @GetMapping("/products")
    fun getProducts() = productService.getProducts()

    @GetMapping("/products/{id}")
    fun getProduct(@PathVariable id: Long) = productService.getProduct(id)

    @PostMapping("/products/{id}/purchase")
    fun purchaseProduct(@PathVariable id: Long, @RequestBody body: PurchaseRequestBody): ProductPurchaseResponseBody {
        val productAndChange = productService.purchaseProduct(id, body.coins)

        return ProductPurchaseResponseBody(productAndChange.first, productAndChange.second)
    }

}