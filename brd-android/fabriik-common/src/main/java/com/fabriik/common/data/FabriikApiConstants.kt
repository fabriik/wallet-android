package com.fabriik.common.data

import android.os.Build
import com.fabriik.common.BuildConfig
import okhttp3.Request

object FabriikApiConstants {

    const val HOST = "${BuildConfig.SERVER_HOST}/blocksatoshi"

    private const val BASE_URL = "https://$HOST"

    const val HOST_WYRE_API = "$BASE_URL/wyre/"
    const val HOST_KYC_API = "$BASE_URL/one/kyc/"
    const val HOST_SWAP_API = "$BASE_URL/exchange/"
    const val HOST_AUTH_API = "$BASE_URL/one/auth/"
    const val HOST_WALLET_API = "$BASE_URL/wallet"
    const val HOST_BLOCKSATOSHI_API = "$BASE_URL/blocksatoshi"

    const val ENDPOINT_CURRENCIES = "$HOST_WALLET_API/currencies"
    const val ENDPOINT_FIAT_CURRENCIES = "$HOST_WALLET_API/fiat_currencies"

    const val URL_SUPPORT_PAGE = "https://app-support.fabriik.com/"
    const val URL_PRIVACY_POLICY = "https://fabriik.com/privacy-policy/"
    const val URL_TERMS_AND_CONDITIONS = "https://fabriik.com/terms-and-conditions/"

    const val HEADER_DEVICE_ID = "X-Device-ID"
    const val HEADER_USER_AGENT = "User-agent"
    const val HEADER_AUTHORIZATION = "Authorization"

    const val SYSTEM_PROPERTY_USER_AGENT = "http.agent"
    const val UA_APP_NAME = "fabriik/wallet/"
    const val UA_PLATFORM = "android/"

    val USER_AGENT_VALUE = getUserAgent()

    private fun getUserAgent(): String {
        val deviceUserAgent = (System.getProperty(SYSTEM_PROPERTY_USER_AGENT) ?: "")
            .split("\\s".toRegex())
            .firstOrNull()

        //example: fabriik/wallet/100000 Dalvik/2.1.0 android/12
        return "${UA_APP_NAME}${BuildConfig.VERSION_CODE} $deviceUserAgent ${UA_PLATFORM}${Build.VERSION.RELEASE}"
    }
}