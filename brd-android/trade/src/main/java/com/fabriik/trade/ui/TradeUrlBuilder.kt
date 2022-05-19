package com.fabriik.trade.ui

import android.net.Uri

object TradeUrlBuilder {

    fun build(supportedCurrencies: List<String>): Uri {
        val merchantId = "NGVQYXnFp13iKtj1"
        val currencies = supportedCurrencies.joinToString(",")

        return Uri.Builder()
            .scheme("https")
            .authority("widget.changelly.com")
            .appendQueryParameter("to", currencies)
            .appendQueryParameter("from", currencies)
            .appendQueryParameter("toDefault", "eth")
            .appendQueryParameter("fromDefault", "btc")
            .appendQueryParameter("amount", "1")
            .appendQueryParameter("theme", "default")
            .appendQueryParameter("merchant_id", merchantId)
            .appendQueryParameter("v", "3")
            .appendQueryParameter("ref_id", "m3RudWCT8RWVKSrp")
            .build()
    }
}