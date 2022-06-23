package com.breadwallet.ui.profile

import android.content.Context
import com.breadwallet.R
import com.breadwallet.tools.security.ProfileManager
import com.breadwallet.ui.profile.ProfileScreen.E
import com.breadwallet.ui.profile.ProfileScreen.F
import drewcarlson.mobius.flow.subtypeEffectHandler
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transform

fun createProfileScreenHandler(
    context: Context,
    profileManager: ProfileManager
) = subtypeEffectHandler<F, E> {

    addFunction<F.LoadOptions> {
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
        E.OnOptionsLoaded(items)
    }

    addFunction<F.LoadProfileData> {
        val profile = profileManager.getProfile()
        if (profile != null) {
            E.OnProfileDataLoaded(profile)
        } else {
            E.RefreshProfile
        }
    }

    addConsumer<F.RefreshProfile> {
        profileManager.updateProfile()
    }
}