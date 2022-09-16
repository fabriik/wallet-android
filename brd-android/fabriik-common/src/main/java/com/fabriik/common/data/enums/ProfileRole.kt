package com.fabriik.common.data.enums

import com.squareup.moshi.Json

enum class ProfileRole {

    @Json(name = "customer")
    CUSTOMER,

    @Json(name = "kyc1")
    KYC_LEVEL_1,

    @Json(name = "kyc2")
    KYC_LEVEL_2,

    @Json(name = "unverified")
    UNVERIFIED,
}