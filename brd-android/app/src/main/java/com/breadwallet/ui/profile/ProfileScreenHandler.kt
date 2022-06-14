package com.breadwallet.ui.profile

import android.content.Context
import com.breadwallet.R
import com.breadwallet.tools.security.BrdUserManager
import com.breadwallet.tools.security.ProfileManager
import com.breadwallet.ui.profile.ProfileScreen.E
import com.breadwallet.ui.profile.ProfileScreen.F
import com.breadwallet.util.errorHandler
import com.fabriik.common.data.Status
import com.fabriik.registration.data.RegistrationApi
import com.spotify.mobius.Connection
import com.spotify.mobius.functions.Consumer
import kotlinx.coroutines.*

class ProfileScreenHandler(
    private val output: Consumer<E>,
    private val context: Context,
    private val profileManager: ProfileManager
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
        launch {
            val profile = profileManager.getProfile()
            if (profile != null) {
                output.accept(E.OnProfileDataLoaded(profile))
            } else {
                output.accept(E.OnProfileDataLoadFailed(null))
            }
        }
    }

    private fun loadOptions() {
        val items = listOf(
            ProfileItem(
                title = context.getString(R.string.MenuButton_security),
                option = ProfileOption.SECURITY_SETTINGS,
                iconResId = R.drawable.ic_security_settings
            ),
            ProfileItem(
                title = context.getString(R.string.Settings_preferences),
                option = ProfileOption.PREFERENCES,
                iconResId = R.drawable.ic_preferences
            )
        )
        output.accept(E.OnOptionsLoaded(items))
    }
}