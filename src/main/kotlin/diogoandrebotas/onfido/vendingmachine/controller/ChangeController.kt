package diogoandrebotas.onfido.vendingmachine.controller

import diogoandrebotas.onfido.vendingmachine.service.ChangeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/change")
class ChangeController(
    private val changeService: ChangeService
) {

    @GetMapping
    fun getChange() = changeService.getChange()

    @PostMapping("/reset")
    fun resetChange() = changeService.resetChange()

}