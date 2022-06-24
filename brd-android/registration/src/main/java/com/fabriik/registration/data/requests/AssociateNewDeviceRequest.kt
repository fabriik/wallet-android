package com.fabriik.registration.data.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssociateNewDeviceRequest(
    @Json(name = "nonce")
    val nonce: String,

    @Json(name = "token")
    val token: String
)