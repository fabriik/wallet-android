package com.fabriik.kyc.data

import com.fabriik.kyc.data.requests.CompleteLevel1VerificationRequest
import com.fabriik.kyc.data.response.CountriesResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface KycService {

    @GET("countries")
    suspend fun getCountries(
        @Query("_locale") locale: String
    ): CountriesResponse

    @POST("basic")
    suspend fun completeLevel1Verification(
        @Body request: CompleteLevel1VerificationRequest
    ): ResponseBody?
}