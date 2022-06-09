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
    KYC_BASIC,

    @Json(name = "kyc2_expired")
    KYC_UNLIMITED_EXPIRED,

    @Json(name = "kyc2_submitted")
    KYC_UNLIMITED_SUBMITTED,

    @Json(name = "kyc2_resubmission_requested")
    KYC_UNLIMITED_RESUBMISSION_REQUESTED,

    @Json(name = "kyc2_declined")
    KYC_UNLIMITED_DECLINED,

    @Json(name = "kyc2")
    KYC_UNLIMITED
}