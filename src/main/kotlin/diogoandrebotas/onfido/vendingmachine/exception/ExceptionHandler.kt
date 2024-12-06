package diogoandrebotas.onfido.vendingmachine.exception

import diogoandrebotas.onfido.vendingmachine.model.http.ErrorResponseBody
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception) =
        getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, exception.message)

    @ExceptionHandler(MissingChangeException::class)
    fun handleMissingChangeException(exception: MissingChangeException) =
        getResponseEntity(HttpStatus.CONFLICT, exception.message)

    @ExceptionHandler(NotEnoughMoneyProvidedException::class)
    fun handleNotEnoughMoneyProvidedException(exception: NotEnoughMoneyProvidedException) =
        getResponseEntity(HttpStatus.BAD_REQUEST, exception.message)

    @ExceptionHandler(ProductNotFoundException::class)
    fun handleProductNotFoundException(exception: ProductNotFoundException) =
        getResponseEntity(HttpStatus.NOT_FOUND, exception.message)

    @ExceptionHandler(ProductOutOfStockException::class)
    fun handleProductOutOfStockException(exception: ProductOutOfStockException) =
        getResponseEntity(HttpStatus.CONFLICT, exception.message)

    private fun getResponseEntity(httpStatus: HttpStatus, message: String?): ResponseEntity<ErrorResponseBody> =
        ResponseEntity
            .status(httpStatus)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                ErrorResponseBody(
                    status = httpStatus.value(),
                    message = message ?: "Unable to retrieve exception message"
                )
            )

}
