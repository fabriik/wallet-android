package com.fabriik.trade.data

import android.content.Context
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.common.data.Resource
import com.fabriik.common.utils.FabriikApiResponseMapper
import com.fabriik.trade.R
import com.fabriik.trade.data.model.SwapBuyTransactionData
import com.fabriik.trade.data.request.CreateSwapOrderRequest
import com.fabriik.trade.data.request.EstimateEthFeeRequest
import com.fabriik.trade.data.response.CreateSwapOrderResponse
import com.fabriik.trade.data.response.ExchangeOrder
import com.fabriik.trade.data.response.QuoteResponse
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class SwapApi(
    private val context: Context,
    private val service: SwapService
) {
    private val responseMapper = FabriikApiResponseMapper()

    suspend fun getSupportedCurrencies(): Resource<List<String>?> {
       return try {
            val response = service.getSupportedCurrencies()
            Resource.success(data = response.currencies)
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

    suspend fun createOrder(baseQuantity: BigDecimal, termQuantity: BigDecimal, quoteId: String, destination: String): Resource<CreateSwapOrderResponse?> {
        return try {
            val response = service.createOrder(
                CreateSwapOrderRequest(
                    quoteId = quoteId,
                    destination = destination,
                    depositQuantity = baseQuantity,
                    withdrawQuantity = termQuantity
                )
            )
            Resource.success(data = response)
        } catch (ex: Exception) {
            val error: Resource<CreateSwapOrderResponse?> = responseMapper.mapError(
                context = context,
                exception = ex
            )

            if (error.message?.contains("expired quote", true) == true) {
                return Resource.error(
                    message = context.getString(R.string.Swap_RequestTimedOut)
                )
            } else {
                error
            }
        }
    }

    suspend fun getExchangeOrder(exchangeId: String): Resource<ExchangeOrder?> {
        return try {
            val response = service.getExchange(exchangeId)
            Resource.success(data = response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun getSwapTransactions(): Resource<List<SwapBuyTransactionData>?> {
        return try {
            val response = service.getExchanges()
            Resource.success(data = response.exchanges)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun estimateEthFee(amount: BigDecimal, currency: String, destination: String): Resource<BigDecimal?> {
        return try {
            val response = service.estimateEthFee(
                EstimateEthFeeRequest(
                    amount, currency, destination
                )
            )
            Resource.success(data = response.fee)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    companion object {

        fun create(context: Context, swapApiInterceptor: SwapApiInterceptor, moshiConverter: MoshiConverterFactory) = SwapApi(
            context = context,
            service = Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .callTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(swapApiInterceptor)
                        .build()
                )
                .baseUrl(FabriikApiConstants.HOST_SWAP_API)
                .addConverterFactory(moshiConverter)
                .build()
                .create(SwapService::class.java)
        )
    }
}
