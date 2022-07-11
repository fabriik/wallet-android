package com.fabriik.trade.data

import android.content.Context
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.common.data.Resource
import com.fabriik.common.utils.FabriikApiResponseMapper
import com.fabriik.trade.data.model.TradingPair
import com.fabriik.trade.data.response.QuoteResponse
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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

    companion object {

        fun create(context: Context, moshiConverter: MoshiConverterFactory) = SwapApi(
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
                .addConverterFactory(moshiConverter)
                .build()
                .create(SwapService::class.java)
        )
    }
}