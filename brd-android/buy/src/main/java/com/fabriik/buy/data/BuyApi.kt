package com.fabriik.buy.data

import android.content.Context
import com.fabriik.buy.R
import com.fabriik.buy.data.enums.PaymentStatus
import com.fabriik.buy.data.model.PaymentInstrument
import com.fabriik.buy.data.request.AddPaymentInstrumentRequest
import com.fabriik.buy.data.request.CreateBuyOrderRequest
import com.fabriik.buy.data.response.AddPaymentInstrumentResponse
import com.fabriik.buy.data.response.CreateBuyOrderResponse
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.common.data.Resource
import com.fabriik.common.utils.FabriikApiResponseMapper
import com.fabriik.trade.data.response.QuoteResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.math.BigDecimal
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

    suspend fun getPaymentStatus(reference: String): Resource<PaymentStatus?> {
        return try {
            val response = service.getPaymentStatus(reference)
            Resource.success(data = response.status)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun getQuote(from: String, to: String): Resource<QuoteResponse?> {
        return try {
            val response = service.getQuote(from, to)
            Resource.success(data = response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun createOrder(baseQuantity: BigDecimal, termQuantity: BigDecimal, quoteId: String, destination: String, sourceInstrumentId: String, nologCvv: String): Resource<CreateBuyOrderResponse?> {
        return try {
            val response = service.createOrder(
                CreateBuyOrderRequest(
                    quoteId = quoteId,
                    destination = destination,
                    depositQuantity = baseQuantity,
                    withdrawQuantity = termQuantity,
                    sourceInstrumentId = sourceInstrumentId,
                    nologCvv = nologCvv
                )
            )
            Resource.success(data = response)
        } catch (ex: Exception) {
            val error: Resource<CreateBuyOrderResponse?> = responseMapper.mapError(
                context = context,
                exception = ex
            )

            if (error.message?.contains("expired quote", true) == true) {
                return Resource.error(
                    message = context.getString(R.string.Swap_Input_Error_QuoteExpired)
                )
            } else {
                error
            }
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