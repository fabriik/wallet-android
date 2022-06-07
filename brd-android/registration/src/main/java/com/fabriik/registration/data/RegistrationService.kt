package com.fabriik.registration.data

import com.fabriik.registration.data.requests.AssociateConfirmRequest
import com.fabriik.registration.data.requests.AssociateRequest
import com.fabriik.registration.data.responses.AssociateResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface RegistrationService {

    @POST("associate")
    suspend fun associateAccount(
        @HeaderMap headers: Map<String, String?>,
        @Body request: AssociateRequest
    ) : AssociateResponse

    @POST("associate/confirm")
    suspend fun associateAccountConfirm(
        @HeaderMap headers: Map<String, String>,
        @Body request: AssociateConfirmRequest
    ) : ResponseBody?

    @POST("associate/resend")
    suspend fun resendAssociateAccountChallenge(
        @HeaderMap headers: Map<String, String>
    ) : ResponseBody
}