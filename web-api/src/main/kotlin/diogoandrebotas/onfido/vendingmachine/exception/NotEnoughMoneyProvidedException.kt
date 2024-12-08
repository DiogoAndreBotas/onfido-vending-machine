package diogoandrebotas.onfido.vendingmachine.exception

class NotEnoughMoneyProvidedException(productCost: String, moneyProvided: String) : RuntimeException(
    "The product costs $productCost and you provided only $moneyProvided"
)
