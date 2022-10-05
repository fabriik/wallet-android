package com.breadwallet.ui.profile

import android.content.Context
import android.util.Log
import com.breadwallet.R
import com.breadwallet.tools.security.ProfileManager
import com.breadwallet.ui.profile.ProfileScreen.E
import com.breadwallet.ui.profile.ProfileScreen.F
import com.fabriik.common.data.model.isKyc2
import drewcarlson.mobius.flow.flowTransformer
import drewcarlson.mobius.flow.subtypeEffectHandler
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transform

fun createProfileScreenHandler(
    context: Context,
    profileManager: ProfileManager
) = subtypeEffectHandler<F, E> {

    addFunction<F.LoadOptions> {
        val items = mutableListOf(
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

        if (it.profile?.isKyc2() == true) {
            items.add(
                0, ProfileItem(
                    title = context.getString(R.string.Profile_PaymentMethod),
                    option = ProfileOption.PAYMENT_METHOD,
                    iconResId = R.drawable.ic_credit_card
                )
            )
        }

        E.OnOptionsLoaded(items)
    }

    addTransformer(handleLoadProfile(profileManager))
}

private fun handleLoadProfile(profileManager: ProfileManager) =
    flowTransformer<F.LoadProfileData, E> { effects ->
        effects.flatMapLatest { profileManager.profileChanges() }
            .mapLatest { profile ->
                if (profile == null) {
                    E.OnProfileDataLoadFailed(profile)
                } else {
                    E.OnProfileDataLoaded(profile)
                }
            }
    }