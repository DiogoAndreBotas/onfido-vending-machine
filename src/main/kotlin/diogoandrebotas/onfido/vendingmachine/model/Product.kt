package diogoandrebotas.onfido.vendingmachine.model

import jakarta.persistence.*

@Entity
@Table
data class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String = "",
    val price: String = "",
    var availableQuantity: Int = 0
)