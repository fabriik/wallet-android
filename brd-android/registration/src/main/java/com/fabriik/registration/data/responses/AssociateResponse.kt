package com.fabriik.registration.data.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssociateResponse(
    @Json(name = "result")
    val result: String,

    @Json(name = "error")
    val error: Any? = null,

    @Json(name = "data")
    val data: Data
)

data class Data(
    @Json(name = "sessionKey")
    val sessionKey: String
)