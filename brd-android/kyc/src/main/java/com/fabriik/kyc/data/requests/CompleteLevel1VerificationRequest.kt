package com.fabriik.kyc.data.requests

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CompleteLevel1VerificationRequest(
    @Json(name = "first_name")
    val firstName: String,

    @Json(name = "last_name")
    val lastName: String,

    @Json(name = "country")
    val country: String,

    @Json(name = "date_of_birth")
    val dateOfBirth: String
)