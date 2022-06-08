package com.fabriik.kyc.data

import com.fabriik.kyc.data.requests.CompleteLevel1VerificationRequest
import com.fabriik.kyc.data.response.CountriesResponse
import com.fabriik.kyc.data.response.DocumentsResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface KycService {

    @GET("countries")
    suspend fun getCountries(
        @Query("_locale") locale: String
    ): CountriesResponse

    @GET("documents")
    suspend fun getDocuments(): DocumentsResponse

    @POST("basic")
    suspend fun completeLevel1Verification(
        @Body request: CompleteLevel1VerificationRequest
    ): ResponseBody?

    @Multipart
    @POST("upload")
    suspend fun uploadPhotos(
        @Body request: CompleteLevel1VerificationRequest
    ): ResponseBody?
}