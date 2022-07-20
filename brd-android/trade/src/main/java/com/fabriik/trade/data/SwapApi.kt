package com.fabriik.trade.data

import android.content.Context
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.common.data.Resource
import com.fabriik.common.utils.FabriikApiResponseMapper
import com.fabriik.trade.data.model.TradingPair
import com.fabriik.trade.data.request.CreateOrderRequest
import com.fabriik.trade.data.response.CreateOrderResponse
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

    suspend fun getTradingPairs(): Resource<List<TradingPair>?> {
       return try {
            val response = service.getTradingPairs()
            val tradingPairs = response.pairs.flatMap {
                listOf(
                    it, it.copy(
                        baseCurrency = it.termCurrency,
                        termCurrency = it.baseCurrency
                    )
                )
            }
            Resource.success(data = tradingPairs)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun getQuote(tradingPair: TradingPair): Resource<QuoteResponse?> {
        return try {
            val response = service.getQuote(tradingPair.name)
            Resource.success(data = response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun createOrder(baseQuantity: BigDecimal, termQuantity: BigDecimal, quoteId: String, destination: String, tradeSide: CreateOrderRequest.TradeSide): Resource<CreateOrderResponse?> {
        return try {
            val response = service.createOrder(
                CreateOrderRequest(
                    quoteId = quoteId,
                    tradeSide = tradeSide,
                    destination = destination,
                    baseQuantity = baseQuantity,
                    termQuantity = termQuantity
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