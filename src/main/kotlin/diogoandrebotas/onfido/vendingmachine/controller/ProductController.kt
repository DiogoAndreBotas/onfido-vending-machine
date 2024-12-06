package diogoandrebotas.onfido.vendingmachine.controller

import diogoandrebotas.onfido.vendingmachine.model.http.ProductPurchaseResponseBody
import diogoandrebotas.onfido.vendingmachine.model.http.PurchaseRequestBody
import diogoandrebotas.onfido.vendingmachine.service.ProductService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun getProducts() = productService.getProducts()

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long) = productService.getProduct(id)

    @PostMapping("/{id}/purchase")
    fun purchaseProduct(@PathVariable id: Long, @RequestBody body: PurchaseRequestBody): ProductPurchaseResponseBody {
        val productAndChange = productService.purchaseProduct(id, body.coins)

        return ProductPurchaseResponseBody(productAndChange.first, productAndChange.second)
    }

    @PostMapping("/reset")
    fun resetProductQuantities() = productService.resetProductQuantities()

}