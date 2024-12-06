package diogoandrebotas.onfido.vendingmachine.exception

class ProductOutOfStockException(name: String) : RuntimeException("The product $name is out of stock")
