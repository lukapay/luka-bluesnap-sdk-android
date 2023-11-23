package com.luka.sdk.bluesnap.lukasdk.models

import com.squareup.moshi.Json

internal data class CardDto(
    @Json(name = "Id") val id: Int,
    @Json(name = "UltimosCuatroDigitos") val last4: String,
    @Json(name="TipoTarjeta") val cardType: String,
    @Json(name="SubTipoTarjeta") val cardSubType: String,
    @Json(name="FechaVencimiento") val expiryDate: String,
    @Json(name = "Pais") val country: String
)

internal data class Card(
    var cardNumber: String = "",
    var cvv: String = "",
    var cardHolderName: String = "",
    var expiryDate: String = "",
    var country: String = "",
    var type: String = "",
    var subType: String = ""
){
    val last4: String
        get() = if(cardNumber.length > 4) cardNumber.takeLast(4) else ""

    val expiryDateFormatted: String
        get() {
            val parts = expiryDate.split("/")
            return listOf(parts[0], "20" + parts[1])
                .joinToString("/")
        }
}