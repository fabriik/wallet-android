package com.fabriik.registration.data

import com.fabriik.common.data.FabriikApiResponse
import com.fabriik.common.data.model.Profile
import com.fabriik.registration.data.requests.AssociateConfirmRequest
import com.fabriik.registration.data.requests.AssociateRequest
import com.fabriik.registration.data.responses.AssociateResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface RegistrationService {

    @POST("associate")
    suspend fun associateAccount(
        @HeaderMap headers: Map<String, String?>,
        @Body request: AssociateRequest
    ): FabriikApiResponse<AssociateResponse?>

    @POST("associate/confirm")
    suspend fun associateAccountConfirm(
        @Body request: AssociateConfirmRequest
    ): Response<Unit>

    @POST("associate/resend")
    suspend fun resendAssociateAccountChallenge(): Response<Unit>

    @GET("profile")
    suspend fun getProfile(): Profile
}