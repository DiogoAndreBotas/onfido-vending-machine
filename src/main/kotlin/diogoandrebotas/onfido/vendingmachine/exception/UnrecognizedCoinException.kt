package diogoandrebotas.onfido.vendingmachine.exception

class UnrecognizedCoinException(coin: String) : RuntimeException("The coin $coin is not accepted")
