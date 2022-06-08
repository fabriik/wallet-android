package com.fabriik.registration.data.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssociateConfirmRequest(
    @Json(name = "confirmation_code")
    val confirmationCode: String
)