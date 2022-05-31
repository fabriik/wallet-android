package com.breadwallet.ui.profile

import com.breadwallet.R
import com.breadwallet.tools.util.bch
import com.breadwallet.tools.util.btc
import com.breadwallet.ui.settings.SettingsOption
import com.breadwallet.ui.profile.ProfileScreen.E
import com.breadwallet.ui.profile.ProfileScreen.F
import com.breadwallet.ui.profile.ProfileScreen.M
import com.breadwallet.ui.settings.SettingsSection
import com.spotify.mobius.Next
import com.spotify.mobius.Next.*
import com.spotify.mobius.Update

object ProfileUpdate : Update<M, E, F>, ProfileScreenUpdateSpec {

    override fun update(
        model: M,
        event: E
    ): Next<M, F> = patch(model, event)
/*
    override fun onOptionsLoaded(
        model: M,
        event: E.OnOptionsLoaded
    ): Next<M, F> = next(model.copy(items = event.options))

    override fun onBackClicked(model: M): Next<M, F> =
        dispatch(setOf(F.GoBack))

    override fun onCloseClicked(model: M): Next<M, F> =
        dispatch(setOf(F.GoBack))

    override fun onVerifyProfileClicked(model: M): Next<M, F> =
        dispatch(setOf(F.GoToKyc))

    /*override fun onProfileVerificationInfoClicked(model: M): Next<M, F> =
        dispatch(
            setOf(
                F.ShowFabriikGenericDialog(
                    FabriikGenericDialogArgs(
                        requestKey = DIALOG_ID_VERIFY_ACCOUNT_INFO,
                        titleRes = R.string.ProfileController_verifyDialogTitle,
                        descriptionRes = R.string.ProfileController_verifyDialogDescription,
                        positive = FabriikGenericDialogArgs.ButtonData(
                            icon = R.drawable.ic_profile_small,
                            titleRes = R.string.ProfileController_verifyAccount,
                            resultKey = DIALOG_RESULT_VERIFY_ACCOUNT
                        )
                    )
                )
            )
        )*/

    /*override fun onProfileVerificationInfoResult(event: E.OnProfileVerificationInfoResult): Next<M, F> {
        val resultKey = event.bundle.getString(FabriikGenericDialog.EXTRA_RESULT)

        return if (resultKey == DIALOG_RESULT_VERIFY_ACCOUNT) {
            dispatch(setOf(F.GoToKyc))
        } else {
            noChange()
        }
    }*/

    @Suppress("ComplexMethod")
    override fun onOptionClicked(
        model: M,
        event: E.OnOptionClicked
    ): Next<M, F> {
        return dispatch(
            setOf(
                when (event.option) {
                    SettingsOption.SCAN_QR -> F.GoToQrScan
                    SettingsOption.KYC -> F.GoToKyc
                    SettingsOption.FEEDBACK -> F.GoToFeedback
                    SettingsOption.PREFERENCES -> F.GoToSection(SettingsSection.PREFERENCES)
                    SettingsOption.SECURITY_SETTINGS -> F.GoToSection(SettingsSection.SECURITY)
                    SettingsOption.SUPPORT -> F.GoToSupport
                    SettingsOption.SUBMIT_REVIEW -> F.GoToGooglePlay
                    SettingsOption.ABOUT -> F.GoToAbout
                    SettingsOption.ATM_FINDER -> F.SendAtmFinderRequest
                    SettingsOption.DEVELOPER_OPTIONS -> F.GoToSection(SettingsSection.DEVELOPER_OPTION)
                    SettingsOption.CURRENCY -> F.GoToDisplayCurrency
                    SettingsOption.BTC_MENU -> F.GoToSection(SettingsSection.BTC_SETTINGS)
                    SettingsOption.BCH_MENU -> F.GoToSection(SettingsSection.BCH_SETTINGS)
                    SettingsOption.SHARE_ANONYMOUS_DATA -> F.GoToShareData
                    SettingsOption.NOTIFICATIONS -> F.GoToNotificationsSettings
                    SettingsOption.REDEEM_PRIVATE_KEY -> F.GoToImportWallet
                    SettingsOption.SYNC_BLOCKCHAIN_BTC -> F.GoToSyncBlockchain(btc)
                    SettingsOption.SYNC_BLOCKCHAIN_BCH -> F.GoToSyncBlockchain(bch)
                    SettingsOption.ENABLE_SEG_WIT -> F.GoToEnableSegWit
                    SettingsOption.VIEW_LEGACY_ADDRESS -> F.GoToLegacyAddress
                    SettingsOption.BTC_NODES -> F.GoToNodeSelector
                    SettingsOption.FINGERPRINT_AUTH -> F.GoToFingerprintAuth
                    SettingsOption.PAPER_KEY -> F.GoToAuthentication
                    SettingsOption.UPDATE_PIN -> F.GoToUpdatePin
                    SettingsOption.WIPE -> F.GoToWipeWallet
                    SettingsOption.ONBOARDING_FLOW -> F.GoToOnboarding
                    SettingsOption.SEND_LOGS -> F.SendLogs
                    SettingsOption.API_SERVER -> F.ShowApiServerDialog
                    SettingsOption.WEB_PLAT_DEBUG_URL -> F.ShowPlatformDebugUrlDialog
                    SettingsOption.WEB_PLAT_BUNDLE -> F.ShowPlatformBundleDialog
                    SettingsOption.TOKEN_BUNDLE -> F.ShowTokenBundleDialog
                    SettingsOption.NATIVE_API_EXPLORER -> F.GoToNativeApiExplorer
                    SettingsOption.FAST_SYNC_BTC -> F.GoToFastSync(btc)
                    SettingsOption.RESET_DEFAULT_CURRENCIES -> F.ResetDefaultCurrencies
                    SettingsOption.WIPE_NO_PROMPT -> F.WipeNoPrompt
                    SettingsOption.ENABLE_ALL_WALLETS -> F.EnableAllWallets
                    SettingsOption.CLEAR_BLOCKCHAIN_DATA -> F.ClearBlockchainData
                    SettingsOption.TOGGLE_RATE_APP_PROMPT -> F.ToggleRateAppPrompt
                    SettingsOption.REFRESH_TOKENS -> F.RefreshTokens
                    SettingsOption.DETAILED_LOGGING -> F.DetailedLogging
                    SettingsOption.VIEW_LOGS -> F.ViewLogs
                    SettingsOption.COPY_PAPER_KEY -> F.CopyPaperKey
                    SettingsOption.METADATA_VIEWER -> F.ViewMetadata
                    SettingsOption.EXPORT_TRANSACTIONS -> F.ShowConfirmExportTransactions
                }
            )
        )
    }*/
}
