package com.fabriik.kyc.data

import com.fabriik.kyc.data.requests.CompleteLevel1VerificationRequest
import com.fabriik.kyc.data.response.CountriesResponse
import com.fabriik.kyc.data.response.DocumentsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
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
    ): Response<Unit>

    @Multipart
    @POST("upload")
    suspend fun uploadPhotos(
        @Part("type") type: RequestBody,
        @Part("document_type") documentType: RequestBody?,
        @Part images: Array<MultipartBody.Part>
    ): ResponseBody

    @POST("session/submit")
    suspend fun submitPhotosForVerification(): ResponseBody
}