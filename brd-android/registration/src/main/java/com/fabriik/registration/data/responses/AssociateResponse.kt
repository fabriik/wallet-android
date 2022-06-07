package com.fabriik.registration.data.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssociateResponse(
    @Json(name = "sessionKey")
    val sessionKey: String
)