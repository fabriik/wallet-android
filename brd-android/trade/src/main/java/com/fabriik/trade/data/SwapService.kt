package com.fabriik.trade.data

import com.fabriik.trade.data.response.QuoteResponse
import com.fabriik.trade.data.response.SupportedTradingPairsResponse
import retrofit2.http.*

interface SwapService {

    @GET("supported-currencies")
    suspend fun getSupportedCurrencies(): SupportedTradingPairsResponse

    @GET("quote")
    suspend fun getQuote(
        @Query("source_currency") sourceCurrency: String,
        @Query("destination_currency") destinationCurrency: String
    ): QuoteResponse
}