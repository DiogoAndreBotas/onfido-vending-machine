package diogoandrebotas.onfido.vendingmachine.exception

class NotEnoughMoneyProvidedException(productCost: Float, moneyProvided: Float) : RuntimeException(
    "The product costs £$productCost and you provided only £$moneyProvided"
)
