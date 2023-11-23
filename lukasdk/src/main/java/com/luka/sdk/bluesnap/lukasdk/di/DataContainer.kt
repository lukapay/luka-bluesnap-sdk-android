package com.luka.sdk.bluesnap.lukasdk.di

import com.luka.sdk.bluesnap.lukasdk.data.LukaRepository
import com.luka.sdk.bluesnap.lukasdk.data.LukaRepositoryImpl

internal object DataContainer {

    private val lukaApi = NetworkContainer.lukaApi

    private var repoInstance: LukaRepository? = null

    val lukaRepository: LukaRepository
        get() {
            if(repoInstance == null) {
                repoInstance = LukaRepositoryImpl(lukaApi)
            }
            return repoInstance!!
        }
}