package com.fabriik.common.data.model

import android.os.Parcelable
import com.fabriik.common.data.enums.KycStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Profile(
    @Json(name = "email")
    val email: String,

    @Json(name = "kyc_status")
    val kycStatus: KycStatus
) : Parcelable

fun Profile?.isUserRegistered() = when(this?.kycStatus) {
    null,
    KycStatus.DEFAULT,
    KycStatus.EMAIL_VERIFICATION_PENDING -> false
    else -> true
}

fun Profile?.canUseBuyTrade() = false/*when(this?.kycStatus) {
    null,
    KycStatus.DEFAULT,
    KycStatus.EMAIL_VERIFIED,
    KycStatus.EMAIL_VERIFICATION_PENDING -> false
    else -> true
}*/