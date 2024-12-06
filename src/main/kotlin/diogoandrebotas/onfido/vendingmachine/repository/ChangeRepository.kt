package diogoandrebotas.onfido.vendingmachine.repository

import diogoandrebotas.onfido.vendingmachine.model.Change
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChangeRepository : JpaRepository<Change, String>