package com.fabriik.kyc.data.enums

import com.squareup.moshi.Json

enum class DocumentType(val id: String) {
    @Json(name = "ID_CARD")
    ID_CARD("ID_CARD"),

    @Json(name = "PASSPORT")
    PASSPORT("PASSPORT"),

    @Json(name = "DRIVERS_LICENSE")
    DRIVING_LICENCE("DRIVERS_LICENSE"),

    @Json(name = "RESIDENCE_PERMIT")
    RESIDENCE_PERMIT("RESIDENCE_PERMIT"),

    SELFIE("SELFIE")
}