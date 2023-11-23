package com.luka.sdk.bluesnap.lukasdk.usecases

class FormatExpiryDateUseCase {

    operator fun invoke(cvv: String) : String {
        return cvv
            .replace(" ", "")
            .replace(regex = "[^0-9]+".toRegex(), "")
            .take(4)
            .chunked(2)
            .joinToString("/")
    }
}