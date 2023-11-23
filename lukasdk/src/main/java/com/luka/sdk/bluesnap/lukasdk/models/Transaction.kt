package com.luka.sdk.bluesnap.lukasdk.models

import com.luka.sdk.bluesnap.lukasdk.LukaBluesnap
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

internal data class Transaction(
    @Json(name = "EmailTarjetaHabiente") val email: String,
    @Json(name = "Monto") val amount: String,
    @Json(name = "Referencia") val reference: String = "",
    @Json(name = "TokenBluesnap") val bsToken: String = LukaBluesnap.instance.session.bsToken,
    @Json(name = "Moneda") val currency: String = "USD",
    @Json(name = "Id") val cardId: Int = 0,
    @Json(name = "IdCanal") val channelId: String = "4",
    @Json(name = "ValidacionTarjeta") val storeCard: Boolean = false,
    @Json(name = "TarjetaCredito") val card: TrxCard? = null,
    @Json(name = "TarjetaHabiente") val cardHolder: CardHolder? = null,
    @Json(name = "IdTraza") val lukaClientId: String = LukaBluesnap.instance.session.clienId,
    @Json(name = "IdTarjetaCredito") val creditCardId: Int = 0
)

data class TrxCard(
    @Json(name = "Id") val id: Int = 0,
    @Json(name = "Bin") val bin: String? = null,
    @Json(name = "Categoria") val category : String = "CONSUMER",
    @Json(name = "Ciudad") val city: String? = null,
    @Json(name = "Description") val description: String = "",
    @Json(name = "Direccion") val direction: String? = null,
    @Json(name = "CodigoPostal") val zipCode: String? = null,
    @Json(name = "EstaBoveda") val storeCard: Boolean = true,
    @Json(name = "Moneda") val currency: String = "USD",
    @Json(name = "Estado") val state: String? = null,
    @Json(name = "Pais") val country: String? = null,
    @Json(name = "FechaVencimiento") val expiryDate: String?,
    @Json(name = "TipoTarjeta") val ccType: String?,
    @Json(name = "SubTipoTarjeta") val ccSubType: String?,
    @Json(name = "UltimosCuatroDigitos") val last4: String?,
    @Json(name = "IdStatus") val idStatus: Int
)

data class TransactionResult(
    val id: Int,
    val merchantTransactionId: Int,
    val traceId: String,
    val amount: Double,
    val lukaClientId: String,
    val paymentNetwork: String
)
@JsonClass(generateAdapter = true)
internal data class TransactionResponse(
    @Json(name = "Exitoso") val success:Boolean?,
    @Json(name = "Continua") val continues: Boolean?,
    @Json(name = "Codigo") val code: Int?,
    @Json(name = "TransaccionId") val transactionId: Int?,
    @Json(name = "TransaccionMerchantId") val transactionMerchantId: Int?,
    @Json(name = "MedioDePago") val paymentNetwork: String?,
    @Json(name = "TrazaId") val traceId: String?,
    @Json(name = "InfoTarjeta") val cardData: CardData?,
    @Json(name = "InfoProceso") val resultInfo: ResultInfo?,
    @Json(name = "Descripcion") val msg: String?,
    @Json(name = "Mensaje") val errorMsg: String?,
    @Json(name = "TarjetaHabiente") val cardHolder: CardHolder?,
    @Json(name = "InfoUsuarioPagador") val infoUser: InfoUser?,
    @Json(name = "Monto") val amount: Double?
) {
    companion object {
        fun fromJson(json: String): TransactionResponse? {
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(TransactionResponse::class.java)
            return adapter.fromJson(json)
        }
    }
}
@JsonClass(generateAdapter = true)
internal data class ResultInfo(
    @Json(name = "EstatusProcesamiento") val status: String?
)
@JsonClass(generateAdapter = true)
internal data class CardHolder(
    @Json(name = "LukapayId") val lukapayId: String?,
    @Json(name = "Nombre") val name: String? = "",
    @Json(name = "Apellido") val lastName: String? = "",
)
@JsonClass(generateAdapter = true)
internal data class InfoUser(
    @Json(name = "email") val email: String?
)

@JsonClass(generateAdapter = true)
internal data class CardData(
    @Json(name = "Id") val id: Int,
    @Json(name = "UltimosCuatroDigitos") val last4: String?,
    @Json(name = "SubTipoTarjeta") val ccSubType: String?,
    @Json(name = "TipoTarjeta") val ccType: String?,
    @Json(name = "FechaVencimiento") val expiryDate: String?,
    @Json(name = "Pais") val country: String?,
)