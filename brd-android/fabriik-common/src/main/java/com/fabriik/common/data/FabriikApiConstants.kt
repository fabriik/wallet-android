package com.fabriik.common.data

import com.fabriik.common.BuildConfig

object FabriikApiConstants {

    const val HOST = "${BuildConfig.SERVER_HOST}/blocksatoshi"

    private const val BASE_URL = "https://$HOST"

    const val HOST_WYRE_API = "$BASE_URL/wyre/"
    const val HOST_KYC_API = "$BASE_URL/one/kyc/"
    const val HOST_AUTH_API = "$BASE_URL/one/auth/"
    const val HOST_WALLET_API = "$BASE_URL/wallet"
    const val HOST_BLOCKSATOSHI_API = "$BASE_URL/blocksatoshi"

    const val ENDPOINT_CURRENCIES = "$HOST_WALLET_API/currencies"

    val SUPPORTED_SWAP_CURRENCIES = arrayListOf(
        "eth", "btc", "bch", "bsv", "usdt20"
    )

    const val URL_PRIVACY_POLICY = "https://fabriik.com/privacy-policy/"
    const val URL_TERMS_AND_CONDITIONS = "https://fabriik.com/terms-and-conditions/"
}