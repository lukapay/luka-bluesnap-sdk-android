package com.luka.sdk.bluesnap.lukasdk.usecases

import com.luka.sdk.bluesnap.lukasdk.data.LukaRepository

internal class GetBsTokenUseCase(
    private val repo: LukaRepository
) {

    suspend operator fun invoke(): String {
        return repo.getBluesnapToken()
    }
}