package com.fabriik.common.data.enums

import com.squareup.moshi.Json

enum class KycStatus {

    @Json(name = "default")
    DEFAULT,

    @Json(name = "email_verification_pending")
    EMAIL_VERIFICATION_PENDING,

    @Json(name = "email_verified")
    EMAIL_VERIFIED,

    @Json(name = "kyc_basic")
    KYC_BASIC,

    @Json(name = "kyc_unlimited_expired")
    KYC_UNLIMITED_EXPIRED,

    @Json(name = "kyc_unlimited_submitted")
    KYC_UNLIMITED_SUBMITTED,

    @Json(name = "kyc_unlimited_resubmission_requested")
    KYC_UNLIMITED_RESUBMISSION_REQUESTED,

    @Json(name = "kyc_unlimited_declined")
    KYC_UNLIMITED_DECLINED,

    @Json(name = "kyc_unlimited")
    KYC_UNLIMITED
}