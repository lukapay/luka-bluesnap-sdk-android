package com.luka.sdk.bluesnap.lukasdk.models

import com.squareup.moshi.Json

data class Credentials(
    @Json(name = "Username") val username: String,
    @Json(name="Password") val password: String
)