package com.fabriik.registration.data

import com.fabriik.common.data.FabriikApiResponse
import com.fabriik.common.data.model.Profile
import com.fabriik.registration.data.requests.AssociateConfirmRequest
import com.fabriik.registration.data.requests.AssociateEmailRequest
import com.fabriik.registration.data.requests.AssociateNewDeviceRequest
import com.fabriik.registration.data.responses.AssociateEmailResponse
import com.fabriik.registration.data.responses.AssociateNewDeviceResponse
import retrofit2.Response
import retrofit2.http.*

interface RegistrationService {

    @POST("associate")
    suspend fun associateEmail(
        @HeaderMap headers: Map<String, String?>,
        @Body request: AssociateEmailRequest
    ): FabriikApiResponse<AssociateEmailResponse?>

    @POST("new-device")
    suspend fun associateNewDevice(
        @HeaderMap headers: Map<String, String?>,
        @Body request: AssociateNewDeviceRequest
    ): AssociateNewDeviceResponse

    @POST("associate/confirm")
    suspend fun associateAccountConfirm(
        @Body request: AssociateConfirmRequest
    ): Response<Unit>

    @POST("associate/resend")
    suspend fun resendAssociateAccountChallenge(): Response<Unit>

    @GET("profile")
    suspend fun getProfile(): Profile

    @DELETE("profile")
    suspend fun deleteProfile()
}