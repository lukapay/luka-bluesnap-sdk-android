package com.luka.sdk.bluesnap.lukasdk.data

import com.bluesnap.androidapi.services.BlueSnapService
import com.bluesnap.androidapi.services.TokenProvider
import com.bluesnap.androidapi.services.TokenServiceCallback
import com.luka.sdk.bluesnap.lukasdk.LukaBluesnap
import com.luka.sdk.bluesnap.lukasdk.Operation
import com.luka.sdk.bluesnap.lukasdk.models.AddCardExceptions
import com.luka.sdk.bluesnap.lukasdk.models.AddCardResult
import com.luka.sdk.bluesnap.lukasdk.models.Card
import com.luka.sdk.bluesnap.lukasdk.models.CardHolder
import com.luka.sdk.bluesnap.lukasdk.models.Credentials
import com.luka.sdk.bluesnap.lukasdk.models.LukaAuthErrorException
import com.luka.sdk.bluesnap.lukasdk.models.LukaAuthenticationError
import com.luka.sdk.bluesnap.lukasdk.models.LukaBluesnapAuthenticationError
import com.luka.sdk.bluesnap.lukasdk.models.LukaCard
import com.luka.sdk.bluesnap.lukasdk.models.PaymentExceptions
import com.luka.sdk.bluesnap.lukasdk.models.Transaction
import com.luka.sdk.bluesnap.lukasdk.models.TransactionResponse
import com.luka.sdk.bluesnap.lukasdk.models.TransactionResult
import com.luka.sdk.bluesnap.lukasdk.models.TrxCard
import com.luka.sdk.bluesnap.lukasdk.network.LukaApi
import com.luka.sdk.bluesnap.lukasdk.utils.bearerToken
import kotlin.math.exp

internal class LukaRepositoryImpl(
    private val lukaApi: LukaApi
) : LukaRepository {

    override suspend fun lukaAuth(username: String, password: String) {
         val result = lukaApi.login(Credentials(username, password))

        val id = result.headers()["id"] ?: throw LukaAuthenticationError
        val token = result.headers()["token"] ?: throw LukaAuthenticationError

        LukaBluesnap.instance.session.clienId = id
        LukaBluesnap.instance.session.lukaToken = token
    }

    override suspend fun getBluesnapToken(): String {
        val result = lukaApi.getBsToken(LukaBluesnap.instance.session.lukaToken.bearerToken())
        val bsToken = result.headers()["bstoken"] ?: throw LukaBluesnapAuthenticationError

        LukaBluesnap.instance.session.bsToken = bsToken
        return bsToken

    }

    override suspend fun processPayment(amount: Double, email: String, customerId: String, card: LukaCard) : TransactionResult {

        lukaAuth(
            LukaBluesnap.instance.config.credentials.username,
            LukaBluesnap.instance.config.credentials.password,
        )

        val bsToken = getBluesnapToken()

        val transaction = Transaction(
            email = email,
            amount = String.format("%.2f", amount),
            storeCard = false,
            card = TrxCard(
                idStatus = 0,
                last4 = card.cardLast4,
                country = card.country.lowercase(),
                ccType = card.cardProcessor.name,
                ccSubType = card.cardSubType.name,
                expiryDate = card.expiryDate,
                id = card.cardId
            ),
            cardHolder = CardHolder(
                lukapayId = customerId
            ),
            bsToken = bsToken,
            creditCardId = card.cardId
        )

        val response  = lukaApi.processPayment(
            LukaBluesnap.instance.session.lukaToken.bearerToken(),
            transaction
        )


        if (response.code() == 400) {
            if (response.errorBody() != null) {
                val errorBody = response.errorBody()!!.string()
                val errorResponse = TransactionResponse.fromJson(errorBody)
                throw PaymentExceptions.SomethingWentWrong(errorResponse?.errorMsg ?: "")
            }

            throw PaymentExceptions.BadRequest
        }


        val result = response.body() ?: throw PaymentExceptions.SomethingWentWrong("Something went wrong. Try again")


        if (result.success != true && result.resultInfo?.status != "success") {
            throw PaymentExceptions.SomethingWentWrong(result.msg ?: "Something went wrong. Try again")
        }

        return TransactionResult(
            id = result.transactionId ?: 0,
            merchantTransactionId = result.transactionMerchantId ?: 0,
            traceId = result.traceId ?: "",
            amount = result.amount ?: 0.0,
            lukaClientId = result.cardHolder?.lukapayId ?: "",
            paymentNetwork = result.paymentNetwork ?: ""
        )
    }

    override suspend fun listCards(customerId: String): List<LukaCard> =
        lukaApi.getLukaCards(
            LukaBluesnap.instance.session.lukaToken.bearerToken(),
            customerId)
            .map {
                LukaCard(
                    it.id,
                    cardLast4 = it.last4,
                    cardProcessor = LukaCard.Processor.valueOf(it.cardType),
                    cardSubType = LukaCard.SubType.valueOf(it.cardSubType),
                    country = it.country,
                    expiryDate = it.expiryDate
                )
            }

    override suspend fun deleteCard(customerId: String, cardId: Int) {
        lukaApi.deleteCard(
            LukaBluesnap.instance.session.lukaToken.bearerToken(),
            cardId,
            customerId
        )
    }

    override suspend fun addCard(email: String, customerId: String?, card: Card) : AddCardResult {

        val transaction = Transaction(
            email = email,
            amount = "1.0",
            storeCard = true,
            card = TrxCard(
                idStatus = 0,
                last4 = card.last4,
                country = card.country,
                ccType = card.type,
                ccSubType = card.subType,
                expiryDate = card.expiryDateFormatted
            ),
            cardHolder = CardHolder(
                lukapayId = customerId
            )
        )

        val response  = lukaApi.processPayment(
            LukaBluesnap.instance.session.lukaToken.bearerToken(),
            transaction
        )


        if (response.code() == 400) {
            if (response.errorBody() != null) {
                val errorBody = response.errorBody()!!.string()
                val errorResponse = TransactionResponse.fromJson(errorBody)
                throw AddCardExceptions.SomethingWentWrong(errorResponse?.errorMsg ?: "")
            }

            throw AddCardExceptions.CardAlreadyAdded
        }


        val result = response.body() ?: throw AddCardExceptions.SomethingWentWrong("Something went wrong. Try again")


        if (result.success != true && result.resultInfo?.status != "success") {
            throw AddCardExceptions.SomethingWentWrong(result.msg ?: "Something went wrong. Try again")
        }

        return AddCardResult(
            lukaCustomerId = result.cardHolder?.lukapayId ?: "",
            card = LukaCard(
                result.cardData?.id ?: 0,
                result.cardData?.last4 ?: "",
                LukaCard.Processor.valueOf(result.cardData?.ccType ?: ""),
                LukaCard.SubType.valueOf(result.cardData?.ccSubType ?: ""),
                result.cardData?.country ?: "",
                result.cardData?.expiryDate ?: "",
            )
        )
    }
}