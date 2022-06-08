package com.fabriik.registration.data.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssociateRequest(
    @Json(name = "email")
    val email: String,

    @Json(name = "token")
    val token: String
)