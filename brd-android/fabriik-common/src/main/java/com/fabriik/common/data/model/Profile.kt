package com.fabriik.common.data.model

import android.os.Parcelable
import com.fabriik.common.data.enums.KycStatus
import com.fabriik.common.data.enums.ProfileRole
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
    val exchangeLimits: ExchangeLimits?,

    @Json(name = "roles")
    val roles: List<ProfileRole?>?,
) : Parcelable

fun Profile?.isRegistrationNeeded() = this == null || noRole(ProfileRole.CUSTOMER)

fun Profile?.isEmailVerificationNeeded() = allRoles(ProfileRole.CUSTOMER, ProfileRole.UNVERIFIED)

fun Profile?.canUseBuy() = isVerifiedCustomer() && hasRole(ProfileRole.KYC_LEVEL_2)

fun Profile?.canUseTrade() =
    isVerifiedCustomer() && anyRole(ProfileRole.KYC_LEVEL_1, ProfileRole.KYC_LEVEL_2)

private fun Profile?.noRole(role: ProfileRole) = !hasRole(role)

fun Profile?.hasRole(role: ProfileRole) = this?.roles?.contains(role) ?: false

private fun Profile?.anyRole(vararg roles: ProfileRole) = roles.any { hasRole(it) }

private fun Profile?.allRoles(vararg roles: ProfileRole) = roles.all { hasRole(it) }

private fun Profile?.isVerifiedCustomer() =
    hasRole(ProfileRole.CUSTOMER) && noRole(ProfileRole.UNVERIFIED)

fun Profile.isKyc1(): Boolean = hasRole(ProfileRole.KYC_LEVEL_1)

fun Profile.isKyc2(): Boolean = hasRole(ProfileRole.KYC_LEVEL_2)