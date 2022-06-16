package com.fabriik.registration.data.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssociateNewDeviceResponse(

    @Json(name = "status")
    val status: AssociateNewDeviceStatus,

    @Json(name = "email")
    val email: String?,

    @Json(name = "sessionKey")
    val sessionKey: String?
)

enum class AssociateNewDeviceStatus {
    @Json(name = "NO_EMAIL")
    NO_EMAIL,

    @Json(name = "SENT")
    SENT
}