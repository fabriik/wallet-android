package com.breadwallet.ui.profile

import android.content.Context
import com.breadwallet.R
import com.breadwallet.ui.profile.ProfileScreen.E
import com.breadwallet.ui.profile.ProfileScreen.F
import com.breadwallet.util.errorHandler
import com.breadwallet.ui.settings.SettingsItem
import com.breadwallet.ui.settings.SettingsOption
import com.fabriik.kyc.data.enums.AccountVerificationStatus
import com.spotify.mobius.Connection
import com.spotify.mobius.functions.Consumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class ProfileScreenHandler(
    private val output: Consumer<E>,
    private val context: Context
) : Connection<F>, CoroutineScope {

    override val coroutineContext = SupervisorJob() + Dispatchers.Default + errorHandler()

    override fun accept(value: F) {
        when (value) {
            F.LoadOptions -> loadOptions()
            F.LoadProfileData -> loadProfileData()
        }
    }

    override fun dispose() {
        coroutineContext.cancel()
    }

    private fun loadProfileData() {
        val profileData = ProfileScreen.ProfileData(
            email = "test@test.com",
            verificationStatus = AccountVerificationStatus.DEFAULT
        )
        output.accept(E.OnProfileDataLoaded(profileData))
    }

    private fun loadOptions() {
        val items = listOf(
            SettingsItem(
                context.getString(R.string.MenuButton_security),
                SettingsOption.SECURITY_SETTINGS,
                R.drawable.ic_security_settings
            ),
            SettingsItem(
                context.getString(R.string.Settings_preferences),
                SettingsOption.PREFERENCES,
                R.drawable.ic_preferences
            )
        )
        output.accept(E.OnOptionsLoaded(items))
    }
}