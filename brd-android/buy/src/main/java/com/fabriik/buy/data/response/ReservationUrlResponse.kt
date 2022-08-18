package com.fabriik.buy.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReservationUrlResponse(
    @Json(name = "url")
    val url: String,

    @Json(name = "reservation")
    val reservation: String
)