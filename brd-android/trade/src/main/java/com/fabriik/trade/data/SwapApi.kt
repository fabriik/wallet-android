package com.fabriik.trade.data

import android.content.Context
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.common.data.Resource
import com.fabriik.common.utils.FabriikApiResponseMapper
import com.fabriik.trade.data.model.TradingPair
import com.fabriik.trade.data.response.QuoteResponse
import com.fabriik.trade.utils.adapter.BigDecimalAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
        /*return try {
            val response = service.getTradingPairs()
            Resource.success(data = response.pairs)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }*/

        return Resource.success(
            listOf(
                TradingPair(
                    name = "BSV-BTC",
                    baseCurrency = "BSV",
                    termCurrency = "BTC",
                    minAmount = BigDecimal("0.001"),
                    maxAmount = BigDecimal("1000000")
                ),
                TradingPair(
                    name = "BTC-USDT",
                    baseCurrency = "BTC",
                    termCurrency = "USDT",
                    minAmount = BigDecimal("0.001"),
                    maxAmount = BigDecimal("1000000")
                ),
            )
        )
    }

    suspend fun getQuote(selectedTradingPair: TradingPair): Resource<QuoteResponse?> {
        /*return try {
            val response = service.getQuote(selectedTradingPair.name)
            Resource.success(data = response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }*/
        return Resource.success(
            if (selectedTradingPair.name == "BSV-BTC") {
                QuoteResponse(
                    securityId = "BSV-BTC",
                    closeAsk = BigDecimal("0.002658"),
                    closeBid = BigDecimal("0.002651"),
                    timestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15),
                    outFeeEstimates = emptyList(),
                    inFeeEstimates = emptyList()
                )
            } else {
                QuoteResponse(
                    securityId = "BTC-USDT",
                    closeAsk = BigDecimal("19784"),
                    closeBid = BigDecimal("19776"),
                    timestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15),
                    outFeeEstimates = emptyList(),
                    inFeeEstimates = emptyList()
                )
            }
        )
    }

    companion object {

        fun create(context: Context) = SwapApi(
            context = context,
            service = Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .callTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(SwapApiInterceptor())
                        .build()
                )
                .baseUrl(FabriikApiConstants.HOST_SWAP_API)
                .addConverterFactory(
                    MoshiConverterFactory.create(
                        Moshi.Builder()
                            .add(BigDecimalAdapter)
                            .addLast(KotlinJsonAdapterFactory())
                            .build()
                    )
                )
                .build()
                .create(SwapService::class.java)
        )
    }
}