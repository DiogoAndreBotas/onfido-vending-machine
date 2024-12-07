package diogoandrebotas.onfido.vendingmachine.exception

class NotEnoughMoneyProvidedException(productCost: Float, moneyProvided: Float) : RuntimeException(
class NotEnoughMoneyProvidedException(productCost: Double, moneyProvided: Double) : RuntimeException(
    "The product costs £$productCost and you provided only £$moneyProvided"
)
