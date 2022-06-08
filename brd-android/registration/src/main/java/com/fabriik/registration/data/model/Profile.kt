package com.fabriik.registration.data.model

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