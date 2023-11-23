package com.luka.sdk.bluesnap.lukasdk.data

import com.luka.sdk.bluesnap.lukasdk.models.AddCardResult
import com.luka.sdk.bluesnap.lukasdk.models.Card
import com.luka.sdk.bluesnap.lukasdk.models.LukaCard
import com.luka.sdk.bluesnap.lukasdk.models.TransactionResponse
import com.luka.sdk.bluesnap.lukasdk.models.TransactionResult

internal interface LukaRepository {
    suspend fun lukaAuth(username: String, password: String)
    suspend fun getBluesnapToken(): String
    suspend fun processPayment(amount: Double, email: String, customerId: String, card: LukaCard) : TransactionResult
    suspend fun listCards(customerId: String) : List<LukaCard>
    suspend fun deleteCard(customerId: String, cardId: Int)
    suspend fun addCard(email: String, customerId: String?, card: Card): AddCardResult
}