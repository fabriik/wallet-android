package com.fabriik.kyc.data.response

import com.fabriik.kyc.data.model.Country
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountriesResponse(
    @Json(name = "countries")
    val countries: List<Country>
)