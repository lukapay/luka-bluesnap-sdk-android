package com.luka.sdk.bluesnap.lukasdk.usecases

import com.luka.sdk.bluesnap.lukasdk.data.LukaRepository
import com.luka.sdk.bluesnap.lukasdk.models.LukaCard

internal class ListCardsUseCase(
    private val repo: LukaRepository
) {

    suspend operator fun invoke(customerId: String): List<LukaCard> {
        return repo.listCards(customerId)
    }
}