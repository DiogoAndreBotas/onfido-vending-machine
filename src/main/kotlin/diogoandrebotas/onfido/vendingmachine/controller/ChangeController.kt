package diogoandrebotas.onfido.vendingmachine.controller

import diogoandrebotas.onfido.vendingmachine.service.ChangeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ChangeController(
    private val changeService: ChangeService
) {

    @GetMapping("/change")
    fun getChange() = changeService.getChange()

}