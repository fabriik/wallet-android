package com.fabriik.common.data.model

import android.os.Parcelable
import com.fabriik.common.data.enums.KycStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class Profile(
    @Json(name = "email")
    val email: String,

    @Json(name = "first_name")
    val firstName: String?,

    @Json(name = "last_name")
    val lastName: String?,

    @Json(name = "country")
    val country: String?,

    @Json(name = "date_of_birth")
    val dateOfBirth: Calendar?,

    @Json(name = "kyc_status")
    val kycStatus: KycStatus,

    @Json(name = "kycFailureReason")
    val kycFailureReason: String?

) : Parcelable

fun Profile?.isUserRegistered() = when(this?.kycStatus) {
    null,
    KycStatus.DEFAULT,
    KycStatus.EMAIL_VERIFICATION_PENDING -> false
    else -> true
}

fun Profile?.canUseBuyTrade() = when(this?.kycStatus) {
    null,
    KycStatus.DEFAULT,
    KycStatus.EMAIL_VERIFIED,
    KycStatus.EMAIL_VERIFICATION_PENDING -> false
    else -> true
}