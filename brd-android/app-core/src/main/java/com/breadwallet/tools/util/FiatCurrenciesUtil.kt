package com.breadwallet.tools.util

import android.content.Context
import com.breadwallet.appcore.R
import com.breadwallet.logger.logError
import com.breadwallet.model.FiatCurrency
import com.breadwallet.tools.manager.BRReportsManager
import com.fabriik.common.data.FabriikApiConstants
import com.platform.APIClient.BRResponse
import com.platform.APIClient.Companion.getInstance
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap

object FiatCurrenciesUtil {
    private const val FIELD_CODE = "code"
    private const val FIELD_NAME = "name"
    private const val FIAT_FILENAME = "fiat-currencies.json"

    private lateinit var context: Context
    private var currencies: List<FiatCurrency> = ArrayList()
    private var currencyMap: Map<String, FiatCurrency> = HashMap()
    private val initLock = Mutex(locked = true)

    suspend fun waitUntilInitialized() = initLock.withLock { Unit }

    /**
     * When the app first starts, fetch our local copy of tokens.json from the resource folder
     *
     * @param context The Context of the caller
     */
    fun initialize(context: Context, forceLoad: Boolean) {
        FiatCurrenciesUtil.context = context

        val tokensFile = File(context.filesDir, FIAT_FILENAME)
        if (!tokensFile.exists() || forceLoad) {
            try {
                initLock.tryLock()
                val currencies = context.resources
                    .openRawResource(R.raw.fiatcurrencies)
                    .reader().use { it.readText() }

                // Copy the APK tokens.json to a file on internal storage
                saveDataToFile(context, currencies, FIAT_FILENAME)
                loadCurrencies(parseJsonToList(currencies))
                initLock.unlock()
            } catch (e: IOException) {
                BRReportsManager.error("Failed to read res/raw/fiatcurrencies.json", e)
            }
        }
        initLock.tryLock()
        fetchCurrenciesFromServer()
        initLock.unlock()
    }

    /**
     * This method can either fetch the list of supported tokens, or fetch a specific token by saleAddress
     * Request the list of tokens we support from the /currencies endpoint
     *
     * @param tokenUrl The URL of the endpoint to get the token metadata from.
     */
    private fun fetchCurrenciesFromServer(tokenUrl: String): BRResponse {
        val request = Request.Builder()
            .get()
            .url(tokenUrl)
            .header(BRConstants.HEADER_CONTENT_TYPE, BRConstants.CONTENT_TYPE_JSON_CHARSET_UTF8)
            .header(BRConstants.HEADER_ACCEPT, BRConstants.CONTENT_TYPE_JSON)
            .build()
        //noinspection deprecation
        return getInstance(context).sendRequest(request, false)
    }

    @Synchronized
    fun getFiatCurrencies(): List<FiatCurrency> {
        if (currencies.isEmpty()) {
            loadCurrencies(getCurrenciesFromFile())
        }
        return currencies
    }

    private fun fetchCurrenciesFromServer() {
        val response = fetchCurrenciesFromServer(FabriikApiConstants.ENDPOINT_FIAT_CURRENCIES)
        if (response.isSuccessful && response.bodyText.isNotEmpty()) {
            // Synchronize on the class object since getFiatCurrencys is static and also synchronizes
            // on the class object rather than on an instance of the class.
            synchronized(FiatCurrency::class.java) {
                val responseBody = response.bodyText

                // Check if the response from the server is valid JSON before trying to save & parse.
                if (Utils.isValidJSON(responseBody)) {
                    saveDataToFile(context, responseBody, FIAT_FILENAME)
                    loadCurrencies(parseJsonToList(responseBody))
                }
            }
        } else {
            logError("failed to fetch tokens: ${response.code}")
        }
    }

    private fun parseJsonToList(jsonString: String): ArrayList<FiatCurrency> {
        val tokenJsonArray = try {
            JSONArray(jsonString)
        } catch (e: JSONException) {
            BRReportsManager.error("Failed to parse Token list JSON.", e)
            JSONArray()
        }
        return List(tokenJsonArray.length()) { i ->
            try {
                tokenJsonArray.getJSONObject(i).asFiatCurrency()
            } catch (e: JSONException) {
                BRReportsManager.error("Failed to parse Token JSON.", e)
                null
            }
        }.filterNotNull().run(::ArrayList)
    }

    private fun saveDataToFile(context: Context, jsonResponse: String, fileName: String) {
        try {
            File(context.filesDir.absolutePath, fileName).writeText(jsonResponse)
        } catch (e: IOException) {
            BRReportsManager.error("Failed to write tokens.json file", e)
        }
    }

    private fun getCurrenciesFromFile(): List<FiatCurrency> = try {
        val file = File(context.filesDir.path, FIAT_FILENAME)
        parseJsonToList(file.readText())
    } catch (e: IOException) {
        BRReportsManager.error("Failed to read tokens.json file", e)
        currencies
    }

    private fun loadCurrencies(currencies: List<FiatCurrency>) {
        FiatCurrenciesUtil.currencies = currencies
        currencyMap = FiatCurrenciesUtil.currencies.associateBy { item ->
            item.code.uppercase()
        }
    }

    private fun JSONObject.asFiatCurrency(): FiatCurrency? = try {
        FiatCurrency(
            code = getString(FIELD_CODE),
            name = getString(FIELD_NAME)
        )
    } catch (e: JSONException) {
        BRReportsManager.error("Fiat currencies JSON: $this")
        BRReportsManager.error("Failed to create FiatCurrency from JSON.", e)
        null
    }
}
