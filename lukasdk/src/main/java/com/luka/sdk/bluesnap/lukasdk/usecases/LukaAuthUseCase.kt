package com.luka.sdk.bluesnap.lukasdk.usecases

import com.luka.sdk.bluesnap.lukasdk.data.LukaRepository

internal class LukaAuthUseCase(
    private val lukaRepository : LukaRepository
) {
    suspend operator fun invoke(username: String, password: String) {
        lukaRepository.lukaAuth(username, password)
    }
}