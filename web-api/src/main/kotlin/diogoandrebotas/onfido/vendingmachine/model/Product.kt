package diogoandrebotas.onfido.vendingmachine.model

import jakarta.persistence.*
import kotlinx.serialization.Serializable

@Entity
@Table
@Serializable
data class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String = "",
    val price: String = "",
    var availableQuantity: Int = 0
)