package com.fabriik.registration.data.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssociateNewDeviceResponse(
    @Json(name = "email")
    val email: String,

    @Json(name = "sessionKey")
    val sessionKey: String
)