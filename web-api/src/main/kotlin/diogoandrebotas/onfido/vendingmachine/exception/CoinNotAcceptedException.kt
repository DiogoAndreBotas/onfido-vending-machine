package diogoandrebotas.onfido.vendingmachine.exception

class CoinNotAcceptedException(coin: String) : RuntimeException("The coin $coin is not accepted")
