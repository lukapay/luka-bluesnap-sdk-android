package com.luka.sdk.bluesnap.lukasdk.models

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
data class BluesnapResponse(
    val issuingCountry: String?,
    val ccType: String?,
    val cardSubType: String?
) {
    companion object {
        fun fromJson(json: String): BluesnapResponse? {
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(BluesnapResponse::class.java)
            return adapter.fromJson(json)
        }
    }
}