package com.fabriik.kyc.data.enums

sealed class AccountVerificationStatus {
    object None : AccountVerificationStatus()
    class Basic(val verified: Boolean) : AccountVerificationStatus()
    class Unlimited(val verified: Boolean) : AccountVerificationStatus()
}