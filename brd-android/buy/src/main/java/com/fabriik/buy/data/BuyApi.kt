package com.fabriik.buy.data

import android.content.Context
import com.fabriik.buy.data.model.PaymentInstrument
import com.fabriik.buy.data.request.AddPaymentInstrumentRequest
import com.fabriik.buy.data.response.AddPaymentInstrumentResponse
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.common.data.Resource
import com.fabriik.common.utils.FabriikApiResponseMapper
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class BuyApi(
    private val context: Context,
    private val service: BuyService
) {

    private val responseMapper = FabriikApiResponseMapper()

    suspend fun addPaymentInstrument(
        token: String, firstName: String, lastName: String, city: String, state: String?,
        zip: String, countryCode: String, address: String
    ): Resource<AddPaymentInstrumentResponse?> {
        return try {
            val response = service.addPaymentInstrument(
                AddPaymentInstrumentRequest(
                    zip = zip,
                    city = city,
                    state = state,
                    token = token,
                    address = address,
                    lastName = lastName,
                    firstName = firstName,
                    countryCode = countryCode,
                )
            )

            Resource.success(data = response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun getPaymentInstruments(): Resource<List<PaymentInstrument>?> {
        return try {
            val response = service.getPaymentInstruments()
            Resource.success(data = response.paymentInstruments)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    companion object {

        fun create(context: Context, buyApiInterceptor: BuyApiInterceptor, moshiConverter: MoshiConverterFactory) =
            BuyApi(
                context = context,
                service = Retrofit.Builder()
                    .client(
                        OkHttpClient.Builder()
                            .readTimeout(30, TimeUnit.SECONDS)
                            .callTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .addInterceptor(buyApiInterceptor)
                            .build()
                    )
                    .baseUrl(FabriikApiConstants.HOST_SWAP_API)
                    .addConverterFactory(moshiConverter)
                    .build()
                    .create(BuyService::class.java)
            )
    }
}