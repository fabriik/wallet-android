package com.fabriik.common.data.enums

import com.squareup.moshi.Json

enum class KycStatus {

    @Json(name = "default")
    DEFAULT,

    @Json(name = "email_verification_pending")
    EMAIL_VERIFICATION_PENDING,

    @Json(name = "email_verified")
    EMAIL_VERIFIED,

    @Json(name = "kyc1")
    KYC1,

    @Json(name = "kyc2_expired")
    KYC2_EXPIRED,

    @Json(name = "kyc2_submitted")
    KYC2_SUBMITTED,

    @Json(name = "kyc2_resubmission_requested")
    KYC2_RESUBMISSION_REQUESTED,

    @Json(name = "kyc2_declined")
    KYC2_DECLINED,

    @Json(name = "kyc2")
    KYC2
}