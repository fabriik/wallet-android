package com.fabriik.kyc.data.enums

import com.squareup.moshi.Json

enum class DocumentType(val id: String) {
    @Json(name = "ID_CARD")
    ID_CARD("id"),

    @Json(name = "PASSPORT")
    PASSPORT("pp"),

    @Json(name = "DRIVERS_LICENSE")
    DRIVING_LICENCE("dl"),

    SELFIE("selfie")
}