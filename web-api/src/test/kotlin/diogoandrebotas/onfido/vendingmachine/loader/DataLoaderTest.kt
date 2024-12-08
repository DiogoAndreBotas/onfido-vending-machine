package diogoandrebotas.onfido.vendingmachine.loader

import diogoandrebotas.onfido.vendingmachine.model.Change
import diogoandrebotas.onfido.vendingmachine.model.Product
import diogoandrebotas.onfido.vendingmachine.repository.ChangeRepository
import diogoandrebotas.onfido.vendingmachine.repository.ProductRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
class DataLoaderTest {

    @Test
    fun `loadProducts loads the set of products onto the database`() {
        val productRepository = mock<ProductRepository> {
            on { saveAll<Product>(any()) } doAnswer { it.getArgument(0) }
        }

        DataLoader(productRepository, mock<ChangeRepository>()).loadProducts()

        verify(productRepository, times(1)).saveAll<Product>(any())
    }

    @Test
    fun `loadChange loads the set of change onto the database`() {
        val changeRepository = mock<ChangeRepository> {
            on { saveAll<Change>(any()) } doAnswer { it.getArgument(0) }
        }

        DataLoader(mock<ProductRepository>(), changeRepository).loadChange()

        verify(changeRepository, times(1)).saveAll<Change>(any())
    }
}