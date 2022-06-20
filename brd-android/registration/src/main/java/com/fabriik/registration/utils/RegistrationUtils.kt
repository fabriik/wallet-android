package com.fabriik.registration.utils

import android.util.Base64
import com.breadwallet.crypto.Key
import com.breadwallet.tools.crypto.CryptoHelper
import com.breadwallet.tools.security.BrdUserManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class RegistrationUtils(
    private val userManager: BrdUserManager
) {

    private val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }

    fun getAssociateRequestHeaders(salt: String, token: String): Map<String, String?> {
        val dateHeaderValue = getDateHeader()

        return mapOf(
            Pair(HEADER_DATE, dateHeaderValue),
            Pair(
                HEADER_SIGNATURE, getSignatureHeader(
                    date = dateHeaderValue,
                    token = token,
                    salt = salt
                )
            )
        )
    }

    private fun getDateHeader() = dateFormat.format(Date())

    private fun getSignatureHeader(date: String, token: String, salt: String): String? {
        val key = getAuthKey() ?: return null
        val signatureSha256 = CryptoHelper.sha256((date + token + salt).toByteArray()) ?: return null
        val signature = CryptoHelper.signBasicDer(signatureSha256, key)
        val signatureEncoded = Base64.encode(signature, Base64.NO_WRAP)
        return String(signatureEncoded).trim()
    }

    private fun getAuthKey(): Key? {
        val key = userManager.getAuthKey() ?: byteArrayOf()
        if (key.isNotEmpty()) {
            return Key.createFromPrivateKeyString(key).orNull()
        }

        return null
    }

    companion object {
        const val HEADER_DATE = "Date"
        const val HEADER_SIGNATURE = "Signature"
    }
}