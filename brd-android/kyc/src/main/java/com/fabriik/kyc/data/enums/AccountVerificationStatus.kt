package com.fabriik.kyc.data.enums

enum class AccountVerificationStatus {
    DEFAULT,
    LEVEL1_VERIFIED,
    LEVEL2_PENDING,
    LEVEL2_VERIFIED,
    LEVEL2_RESUBMIT,
    LEVEL2_DECLINED,
}