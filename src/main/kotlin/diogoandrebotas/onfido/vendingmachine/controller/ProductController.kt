package diogoandrebotas.onfido.vendingmachine.controller

import diogoandrebotas.onfido.vendingmachine.model.http.ProductPurchaseResponseBody
import diogoandrebotas.onfido.vendingmachine.model.http.ProductPurchaseRequestBody
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
    fun purchaseProduct(@PathVariable id: Long, @RequestBody body: ProductPurchaseRequestBody): ProductPurchaseResponseBody {
        return productService.purchaseProduct(id, body.coins).let {
            ProductPurchaseResponseBody(it.product, it.change)
        }
    }

    @PostMapping("/reset")
    fun resetProductQuantities() = productService.resetProductQuantities()

}