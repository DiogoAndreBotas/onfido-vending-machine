package diogoandrebotas.onfido.vendingmachine.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kotlinx.serialization.Serializable

@Entity
@Table
@Serializable
data class Change(
    @Id
    val coin: String = "",
    var quantity: Int = 0
)