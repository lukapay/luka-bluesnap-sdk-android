package com.luka.sdk.bluesnap.lukasdk.usecases

import com.luka.sdk.bluesnap.lukasdk.data.LukaRepository
internal class DeleteCardUseCase (
    private val repo: LukaRepository
) {

    suspend operator fun invoke(customerId: String, cardId: Int) {
        return repo.deleteCard(customerId, cardId)
    }
}