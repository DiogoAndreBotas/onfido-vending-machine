package diogoandrebotas.onfido.vendingmachine.exception

// TODO remove .0 when there are no decimal values in parameters
class NotEnoughMoneyProvidedException(productCost: Double, moneyProvided: Double) : RuntimeException(
    "The product costs £$productCost and you provided only £$moneyProvided"
)
