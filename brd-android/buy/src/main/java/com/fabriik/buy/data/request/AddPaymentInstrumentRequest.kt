package com.fabriik.buy.data.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class AddPaymentInstrumentRequest(
    @Json(name = "token")
    val token: String,

    @Json(name = "first_name")
    val firstName: String,

    @Json(name = "last_name")
    val lastName: String,

    @Json(name = "country_code")
    val countryCode: String,

    @Json(name = "state")
    val state: String? = null,

    @Json(name = "city")
    val city: String,

    @Json(name = "zip")
    val zip: String,

    @Json(name = "address")
    val address: String
)
