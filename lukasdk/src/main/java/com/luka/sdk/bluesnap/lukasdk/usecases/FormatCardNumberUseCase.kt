package com.luka.sdk.bluesnap.lukasdk.usecases

class FormatCardNumberUseCase {

    operator fun invoke(cardNumber: String) : String {
        return cardNumber.replace(" ", "")
            .replace(regex = "[^0-9]+".toRegex(), "")
            .chunked(4)
            .joinToString(" ")
    }
}