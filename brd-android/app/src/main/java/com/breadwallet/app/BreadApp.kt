/**
 * BreadWallet
 *
 * Created by Drew Carlson <drew.carlson@breadwallet.com> on 9/23/19.
 * Copyright (c) 2019 breadwallet LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.breadwallet.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.VisibleForTesting
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.core.content.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.bdb.api.AndroidBdbAuthProvider
import com.brd.api.AndroidBRDAuthProvider
import com.brd.api.BRDApiClient
import com.brd.bakerapi.BakersApiClient
import com.brd.prefs.AndroidPreferences
import com.brd.prefs.Preferences
import com.breadwallet.BuildConfig
import com.breadwallet.breadbox.*
import com.breadwallet.corecrypto.CryptoApiProvider
import com.breadwallet.crypto.CryptoApi
import com.breadwallet.crypto.WalletManagerMode
import com.breadwallet.crypto.blockchaindb.BlockchainDb
import com.breadwallet.installHooks
import com.breadwallet.logger.logDebug
import com.breadwallet.logger.logError
import com.breadwallet.platform.interfaces.AccountMetaDataProvider
import com.breadwallet.repository.ExperimentsRepository
import com.breadwallet.repository.ExperimentsRepositoryImpl
import com.breadwallet.repository.RatesRepository
import com.breadwallet.tools.crypto.Base32
import com.breadwallet.tools.crypto.CryptoHelper
import com.breadwallet.tools.manager.*
import com.breadwallet.tools.security.*
import com.breadwallet.tools.services.BRDFirebaseMessagingService
import com.breadwallet.tools.util.*
import com.breadwallet.ui.uigift.GiftBackup
import com.breadwallet.ui.uigift.SharedPrefsGiftBackup
import com.breadwallet.util.*
import com.breadwallet.util.usermetrics.UserMetricsUtil
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.registration.data.RegistrationApi
import com.fabriik.registration.data.RegistrationApiInterceptor
import com.fabriik.registration.utils.RegistrationUtils
import com.fabriik.registration.utils.UserSessionManager
import com.platform.APIClient
import com.platform.HTTPServer
import com.platform.interfaces.KVStoreProvider
import com.platform.interfaces.MetaDataManager
import com.platform.interfaces.WalletProvider
import com.platform.sqlite.PlatformSqliteHelper
import com.platform.tools.KVStoreManager
import com.platform.tools.SessionHolder
import com.platform.tools.TokenHolder
import drewcarlson.blockset.BdbService
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import org.kodein.di.DKodein
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.direct
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton
import java.io.File
import java.io.UnsupportedEncodingException
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

private const val LOCK_TIMEOUT = 180_000L // 3 minutes in milliseconds
private const val ENCRYPTED_PREFS_FILE = "crypto_shared_prefs"
private const val ENCRYPTED_GIFT_BACKUP_FILE = "gift_shared_prefs"
private const val WALLETKIT_DATA_DIR_NAME = "cryptocore"
private const val CONNECTION_TIMEOUT_SECONDS = 60

@Suppress("TooManyFunctions")
class BreadApp : Application(), KodeinAware, CameraXConfig.Provider {

    companion object {
        init {
            CryptoApi.initialize(CryptoApiProvider.getInstance())
        }

        // The wallet ID is in the form "xxxx xxxx xxxx xxxx" where x is a lowercase letter or a number.
        private const val WALLET_ID_PATTERN = "^[a-z0-9 ]*$"
        private const val WALLET_ID_SEPARATOR = " "
        private const val NUMBER_OF_BYTES_FOR_SHA256_NEEDED = 10

        @SuppressLint("StaticFieldLeak")
        private lateinit var mInstance: BreadApp

        @SuppressLint("StaticFieldLeak")
        private var mCurrentActivity: Activity? = null

        /** [CoroutineScope] matching the lifetime of the application. */
        val applicationScope = CoroutineScope(
            SupervisorJob() + Dispatchers.Default + errorHandler("applicationScope")
        )

        private val startedScope = CoroutineScope(
            SupervisorJob() + Dispatchers.Default + errorHandler("startedScope")
        )

        fun getBreadBox(): BreadBox = mInstance.direct.instance()

        // TODO: Find better place/means for this
        fun getDefaultEnabledWallets() = when {
            BuildConfig.BITCOIN_TESTNET -> listOf(
                "bitcoin-testnet:__native__",
                "ethereum-goerli:__native__"
            )
            else -> listOf(
                "bitcoinsv-mainnet:__native__",
                "bitcoin-mainnet:__native__",
                "ethereum-mainnet:__native__"
            )
        }

        fun getDefaultWalletModes() = when {
            BuildConfig.BITCOIN_TESTNET -> mapOf(
                "bitcoin-testnet:__native__" to WalletManagerMode.API_ONLY,
                "ethereum-goerli:__native__" to WalletManagerMode.API_ONLY
            )
            else -> mapOf(
                "bitcoin-mainnet:__native__" to WalletManagerMode.API_ONLY,
                "ethereum-mainnet:__native__" to WalletManagerMode.API_ONLY,
                "bitcoinsv-mainnet:__native__" to WalletManagerMode.API_ONLY,
            )
        }

        /**
         * Initialize the wallet id (rewards id), and save it in the SharedPreferences.
         */
        private fun initializeWalletId() {
            applicationScope.launch(Dispatchers.Main) {
                val walletId = getBreadBox()
                    .wallets(false)
                    .mapNotNull { wallets ->
                        wallets.find { it.currency.code.isEthereum() }
                    }
                    .take(1)
                    .map { generateWalletId(it.target.toString()) }
                    .flowOn(Dispatchers.Default)
                    .first()
                if (walletId.isNullOrBlank() || !walletId.matches(WALLET_ID_PATTERN.toRegex())) {
                    val error = IllegalStateException("Generated corrupt walletId: $walletId")
                    BRReportsManager.reportBug(error)
                }
                BRSharedPrefs.putWalletRewardId(id = walletId ?: "")
            }
        }

        /**
         * Generates the wallet id (rewards id) based on the Ethereum address. The format of the id is
         * "xxxx xxxx xxxx xxxx", where x is a lowercase letter or a number.
         *
         * @return The wallet id.
         */
        // TODO: This entire operation should be moved into a separate class.
        @Synchronized
        @Suppress("ReturnCount")
        fun generateWalletId(address: String): String? {
            try {
                // Remove the first 2 characters i.e. 0x
                val rawAddress = address.drop(2)

                // Get the address bytes.
                val addressBytes = rawAddress.toByteArray()

                // Run SHA256 on the address bytes.
                val sha256Address = CryptoHelper.sha256(addressBytes) ?: byteArrayOf()
                if (sha256Address.isEmpty()) {
                    BRReportsManager.reportBug(IllegalAccessException("Failed to generate SHA256 hash."))
                    return null
                }

                // Get the first 10 bytes of the SHA256 hash.
                val firstTenBytes =
                    sha256Address.sliceArray(0 until NUMBER_OF_BYTES_FOR_SHA256_NEEDED)

                // Convert the first 10 bytes to a lower case string.
                val base32String = Base32.encode(firstTenBytes).toLowerCase(Locale.ROOT)

                // Insert a space every 4 chars to match the specified format.
                val builder = StringBuilder()
                val matcher = Pattern.compile(".{1,4}").matcher(base32String)
                var separator = ""
                while (matcher.find()) {
                    val piece = base32String.substring(matcher.start(), matcher.end())
                    builder.append(separator + piece)
                    separator = WALLET_ID_SEPARATOR
                }
                return builder.toString()
            } catch (e: UnsupportedEncodingException) {
                logError("Unable to get address bytes.", e)
                return null
            }
        }

        // TODO: Refactor so this does not store the current activity like this.
        @JvmStatic
        @Deprecated("")
        fun getBreadContext(): Context {
            var app: Context? = mCurrentActivity
            if (app == null) {
                app = mInstance
            }
            return app
        }

        // TODO: Refactor so this does not store the current activity like this.
        @JvmStatic
        fun setBreadContext(app: Activity?) {
            mCurrentActivity = app
        }

        /** Provides access to [DKodein]. Meant only for Java compatibility. **/
        @JvmStatic
        fun getKodeinInstance(): DKodein {
            return mInstance.direct
        }
    }

    override val kodein by Kodein.lazy {
        importOnce(androidXModule(this@BreadApp))

        bind<CryptoUriParser>() with singleton {
            CryptoUriParser(instance())
        }

        bind<APIClient>() with singleton {
            val interceptor = instance<FabriikAuthInterceptor>()
            APIClient(this@BreadApp, direct.instance(), interceptor, createHttpHeaders())
        }

        bind<BrdUserManager>() with singleton {
            CryptoUserManager(this@BreadApp, ::createCryptoEncryptedPrefs, instance())
        }

        bind<GiftBackup>() with singleton {
            SharedPrefsGiftBackup(::createGiftBackupEncryptedPrefs)
        }

        bind<KVStoreProvider>() with singleton {
            KVStoreManager(this@BreadApp)
        }

        val localMetadataManager by lazy { MetaDataManager(direct.instance()) }

        bind<WalletProvider>() with singleton { localMetadataManager }

        bind<AccountMetaDataProvider>() with singleton { localMetadataManager }

        bind<OkHttpClient>() with singleton { OkHttpClient() }

        bind<BdbAuthInterceptor>() with singleton {
            val httpClient = instance<OkHttpClient>()
            BdbAuthInterceptor(httpClient, direct.instance())
        }

        bind<FabriikAuthInterceptor>() with singleton {
            FabriikAuthInterceptor(
                this@BreadApp,
                applicationScope
            )
        }

        bind<BlockchainDb>() with singleton {
            val httpClient = instance<OkHttpClient>()
            val authInterceptor = instance<BdbAuthInterceptor>()
            val fabriikAuthInterceptor = instance<FabriikAuthInterceptor>()

            BlockchainDb(
                httpClient.newBuilder()
                    .readTimeout(CONNECTION_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
                    .writeTimeout(CONNECTION_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
                    .connectTimeout(CONNECTION_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
                    .addInterceptor(authInterceptor)
                    .addInterceptor(fabriikAuthInterceptor)
                    .build(),
                FabriikApiConstants.HOST_BLOCKSATOSHI_API,
                FabriikApiConstants.HOST_WALLET_API
            )
        }

        bind<PayIdService>() with singleton {
            PayIdService(instance())
        }

        bind<FioService>() with singleton {
            FioService(instance())
        }

        bind<HttpClient>() with singleton {
            HttpClient(OkHttp) {
                engine {
                    config {
                        retryOnConnectionFailure(true)
                    }
                }
            }
        }

        bind<UnstoppableDomainService>() with singleton {
            UnstoppableDomainService(
                BdbService.create(
                    instance(),
                    authProvider = AndroidBdbAuthProvider(instance())
                )
            )
        }

        bind<AddressResolverServiceLocator>() with singleton {
            AddressResolverServiceLocator(
                instance(),
                instance(),
                instance()
            )
        }

        bind<BreadBox>() with singleton {
            CoreBreadBox(
                File(filesDir, WALLETKIT_DATA_DIR_NAME),
                !BuildConfig.BITCOIN_TESTNET,
                instance(),
                instance(),
                instance()
            )
        }

        bind<ExperimentsRepository>() with singleton { ExperimentsRepositoryImpl }

        bind<RatesRepository>() with singleton { RatesRepository.getInstance(this@BreadApp) }

        bind<RatesFetcher>() with singleton {
            RatesFetcher(
                instance(),
                instance(),
                this@BreadApp
            )
        }

        bind<ConversionTracker>() with singleton {
            ConversionTracker(instance())
        }

        bind<GiftTracker>() with singleton {
            GiftTracker(instance(), instance())
        }

        bind<ConnectivityStateProvider>() with singleton {
            val connectivityManager =
                this@BreadApp.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                NetworkCallbacksConnectivityStateProvider(connectivityManager)
            } else {
                InternetManager(connectivityManager,this@BreadApp)
            }
        }

        bind<SupportManager>() with singleton {
            SupportManager(
                this@BreadApp,
                instance(),
                instance()
            )
        }

        bind<BRDApiClient>() with singleton {
            BRDApiClient.create(AndroidBRDAuthProvider(instance()))
        }

        bind<RegistrationApi>() with singleton {
            RegistrationApi.create(
                this@BreadApp,
                instance()
            )
        }

        bind<RegistrationUtils>() with singleton {
            RegistrationUtils(instance())
        }

        bind<RegistrationApiInterceptor>() with singleton {
            RegistrationApiInterceptor(
                this@BreadApp,
                applicationScope
            )
        }

        bind<ProfileManager>() with singleton {
            ProfileManagerImpl(
                applicationScope,
                instance(),
                instance()
            )
        }

        bind<Preferences>() with singleton {
            val prefs = getSharedPreferences(BRSharedPrefs.PREFS_NAME, Context.MODE_PRIVATE)
            AndroidPreferences(prefs)
        }

        bind<BakersApiClient>() with singleton {
            BakersApiClient.create(instance())
        }
    }

    private var accountLockJob: Job? = null

    private val apiClient by instance<APIClient>()
    private val giftTracker by instance<GiftTracker>()
    private val userManager by instance<BrdUserManager>()
    private val ratesFetcher by instance<RatesFetcher>()
    private val accountMetaData by instance<AccountMetaDataProvider>()
    private val conversionTracker by instance<ConversionTracker>()
    private val connectivityStateProvider by instance<ConnectivityStateProvider>()
    private val CHANNEL = "kyc-platform-channels"
    override fun onCreate() {
        super.onCreate()
        installHooks()
        mInstance = this

        BRKeyStore.provideContext(this)
        BRClipboardManager.provideContext(this)
        BRSharedPrefs.initialize(this, applicationScope)
        TokenHolder.provideContext(this)
        SessionHolder.provideContext(this)
        UserSessionManager.provideContext(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(ApplicationLifecycleObserver())
        ApplicationLifecycleObserver.addApplicationLifecycleListener { event ->
            logDebug(event.name)
            when (event) {
                Lifecycle.Event.ON_START -> handleOnStart()
                Lifecycle.Event.ON_STOP -> handleOnStop()
                Lifecycle.Event.ON_DESTROY -> handleOnDestroy()
                else -> Unit
            }
        }

        applicationScope.launch {
            ServerBundlesHelper.extractBundlesIfNeeded(mInstance)
            TokenUtil.initialize(mInstance, false, !BuildConfig.BITCOIN_TESTNET)
        }

        // Start our local server as soon as the application instance is created, since we need to
        // display support WebViews during onboarding.
        HTTPServer.getInstance().startServer(this)
    }

    /**
     * Each time the app resumes, check to see if the device state is valid.
     * Even if the wallet is not initialized, we may need tell the user to enable the password.
     */
    private fun handleOnStart() {
        accountLockJob?.cancel()
        BreadBoxCloseWorker.cancelEnqueuedWork()
        val breadBox = getBreadBox()
        userManager
            .stateChanges()
            .distinctUntilChanged()
            .filterIsInstance<BrdUserState.Enabled>()
            .onEach {
                if (!userManager.isMigrationRequired()) {
                    startWithInitializedWallet(breadBox)
                }
            }
            .launchIn(startedScope)
    }

    private fun handleOnStop() {
        if (userManager.isInitialized()) {
            accountLockJob = applicationScope.launch {
                delay(LOCK_TIMEOUT)
                userManager.lock()
            }
            BreadBoxCloseWorker.enqueueWork()
            applicationScope.launch {
                EventUtils.saveEvents(this@BreadApp)
                EventUtils.pushToServer(this@BreadApp)
            }

        }
        logDebug("Shutting down HTTPServer.")
        HTTPServer.getInstance().stopServer()

        startedScope.coroutineContext.cancelChildren()
    }

    private fun handleOnDestroy() {
        if (HTTPServer.getInstance().isRunning) {
            logDebug("Shutting down HTTPServer.")
            HTTPServer.getInstance().stopServer()
        }

        connectivityStateProvider.close()
        getBreadBox().apply { if (isOpen) close() }
        applicationScope.cancel()
    }

    fun startWithInitializedWallet(breadBox: BreadBox, migrate: Boolean = false) {
        val context = mInstance.applicationContext
        incrementAppForegroundedCounter()

        if (!breadBox.isOpen) {
            val account = checkNotNull(userManager.getAccount()) {
                "Wallet is initialized but Account is null"
            }

            breadBox.open(account)
        }

        initializeWalletId()
        BRDFirebaseMessagingService.initialize(context)
        HTTPServer.getInstance().startServer(this)
        apiClient.updatePlatform(applicationScope)
        applicationScope.launch {
            UserMetricsUtil.makeUserMetricsRequest(context)
        }

        startedScope.launch {
            accountMetaData.recoverAll(migrate)
            giftTracker.checkUnclaimedGifts()
        }

        ratesFetcher.start(startedScope)
        
        conversionTracker.start(startedScope)
    }

    private fun incrementAppForegroundedCounter() {
        BRSharedPrefs.putInt(
            BRSharedPrefs.APP_FOREGROUNDED_COUNT,
            BRSharedPrefs.getInt(BRSharedPrefs.APP_FOREGROUNDED_COUNT, 0) + 1
        )
    }

    private fun createCryptoEncryptedPrefs(): SharedPreferences? = createEncryptedPrefs(ENCRYPTED_PREFS_FILE)
    private fun createGiftBackupEncryptedPrefs(): SharedPreferences? = createEncryptedPrefs(ENCRYPTED_GIFT_BACKUP_FILE)

    private fun createEncryptedPrefs(fileName: String): SharedPreferences? {
        val masterKeys = try {
            MasterKey.Builder(this)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        } catch (e: Throwable) {
            BRReportsManager.error("Failed to create Master Keys", e)
            return null
        }

        return try {
            EncryptedSharedPreferences.create(
                this@BreadApp,
                fileName,
                masterKeys,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Throwable) {
            BRReportsManager.error("Failed to create Encrypted Shared Preferences", e)
            null
        }
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }

    @VisibleForTesting
    fun clearApplicationData() {
        try {
            startedScope.coroutineContext.cancelChildren()
            applicationScope.coroutineContext.cancelChildren()
            val breadBox = direct.instance<BreadBox>()
            if (breadBox.isOpen) {
                breadBox.close()
            }
            (userManager as CryptoUserManager).wipeAccount()

            File(filesDir, WALLETKIT_DATA_DIR_NAME).deleteRecursively()

            PlatformSqliteHelper.getInstance(this)
                .writableDatabase
                .delete(PlatformSqliteHelper.KV_STORE_TABLE_NAME, null, null)

            getSharedPreferences(BRSharedPrefs.PREFS_NAME, Context.MODE_PRIVATE).edit { clear() }
        } catch (e: Throwable) {
            logError("Failed to clear application data", e)
        }
    }

    fun createHttpHeaders(): Map<String, String> {
        // Split the default device user agent string by spaces and take the first string.
        // Example user agent string: "Dalvik/1.6.0 (Linux; U;Android 5.1; LG-F320SBuild/KOT49I.F320S22g) Android/9"
        // We only want: "Dalvik/1.6.0"
        val deviceUserAgent =
            (System.getProperty(APIClient.SYSTEM_PROPERTY_USER_AGENT) ?: "")
                .split("\\s".toRegex())
                .firstOrNull()

        // The BRD server expects the following user agent: appName/appVersion engine/engineVersion plaform/plaformVersion
        val brdUserAgent = "${APIClient.UA_APP_NAME}${BuildConfig.VERSION_CODE} $deviceUserAgent ${APIClient.UA_PLATFORM}${Build.VERSION.RELEASE}"

        return mapOf(
            APIClient.HEADER_IS_INTERNAL to if (BuildConfig.IS_INTERNAL_BUILD) BRConstants.TRUE else BRConstants.FALSE,
            APIClient.HEADER_TESTFLIGHT to if (BuildConfig.DEBUG) BRConstants.TRUE else BRConstants.FALSE,
            APIClient.HEADER_TESTNET to if (BuildConfig.BITCOIN_TESTNET) BRConstants.TRUE else BRConstants.FALSE,
            APIClient.HEADER_USER_AGENT to brdUserAgent
        )
    }
}
