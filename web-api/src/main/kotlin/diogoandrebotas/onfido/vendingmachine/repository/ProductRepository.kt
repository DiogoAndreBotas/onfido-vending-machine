package diogoandrebotas.onfido.vendingmachine.repository

import diogoandrebotas.onfido.vendingmachine.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long>