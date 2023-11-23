package com.luka.sdk.bluesnap.lukasdk.usecases

import com.luka.sdk.bluesnap.lukasdk.data.LukaRepository
import com.luka.sdk.bluesnap.lukasdk.models.AddCardResult
import com.luka.sdk.bluesnap.lukasdk.models.Card
import com.luka.sdk.bluesnap.lukasdk.models.TransactionResponse

internal class AddCardUseCase(
    private val repo: LukaRepository
) {

    suspend operator fun invoke(email: String, customerId: String?, card: Card): AddCardResult {
        return repo.addCard(email, customerId, card)
    }
}