package diogoandrebotas.onfido.vendingmachine.exception

class ProductNotFoundException(id: Long) : RuntimeException("The product with the ID $id is not available")
