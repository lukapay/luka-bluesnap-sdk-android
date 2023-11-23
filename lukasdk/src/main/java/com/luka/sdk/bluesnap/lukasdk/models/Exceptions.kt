package com.luka.sdk.bluesnap.lukasdk.models


class LukaAuthErrorException(
    val msg: String
): Exception()

object LukaBluesnapAuthenticationError : Exception("No valid authentication was provided")
object LukaAuthenticationError : Exception("No valid authentication was provided for LukaAPI")

sealed class AddCardExceptions(msg: String): Exception(msg) {
    data object CardAlreadyAdded : AddCardExceptions("No valid authentication was provided for LukaAPI")
    class SomethingWentWrong(msg: String) : AddCardExceptions(msg = msg)
}

sealed class PaymentExceptions(msg: String): Exception(msg) {
    data object BadRequest : PaymentExceptions("Transaction failed. Try again later.")
    class SomethingWentWrong(msg: String) : PaymentExceptions(msg = msg)
}
