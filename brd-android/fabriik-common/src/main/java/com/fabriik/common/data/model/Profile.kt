package com.fabriik.common.data.model

import android.os.Parcelable
import com.fabriik.common.data.enums.KycStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
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

    @Json(name = "kyc_failure_reason")
    val kycFailureReason: String?,

    @Json(name = "exchange_limits")
    val exchangeLimits: ExchangeLimits?
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

fun Profile?.nextExchangeLimit(): BigDecimal = this?.exchangeLimits?.nextExchangeLimit ?: BigDecimal.ZERO

fun Profile?.availableDailyLimit(): BigDecimal = this?.exchangeLimits?.availableDaily() ?: BigDecimal.ZERO

fun Profile?.availableLifetimeLimit(): BigDecimal = this?.exchangeLimits?.availableLifetime() ?: BigDecimal.ZERO