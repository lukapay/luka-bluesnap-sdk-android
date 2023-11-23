package com.luka.sdk.bluesnap.lukasdk.models

class LukaCard(
    val cardId: Int,
    val cardLast4: String,
    val cardProcessor: Processor,
    val cardSubType: SubType,
    val country: String,
    val expiryDate: String
) {


    enum class Processor {
        VISA,
        MASTERCARD,
        AMEX,
        UNKNOWN
    }

    enum class SubType {
        CREDIT,
        DEBIT,
        OTHER
    }
}