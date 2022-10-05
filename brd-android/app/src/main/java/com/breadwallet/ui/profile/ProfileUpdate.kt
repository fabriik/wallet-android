package com.breadwallet.ui.profile

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

    override fun onCloseClicked(model: M): Next<M, F> =
        dispatch(setOf(F.GoBack))

    override fun onVerifyProfileClicked(model: M): Next<M, F> =
        dispatch(setOf(F.GoToKyc))

    override fun onUpgradeLimitsClicked(model: M): Next<M, F> =
        dispatch(setOf(F.GoToKyc))

    override fun onVerificationMoreInfoClicked(model: M): Next<M, F> =
        dispatch(setOf(F.ShowInfoDialog))

    override fun onVerificationDeclinedInfoClicked(model: M): Next<M, F> =
        dispatch(setOf(F.GoToKyc))

    override fun onChangeEmailClicked(model: M): Next<M, F> =
        dispatch(emptySet())

    override fun onOptionsLoaded(
        model: M,
        event: E.OnOptionsLoaded
    ): Next<M, F> = next(model.copy(items = event.options))

    override fun onOptionClicked(model: M, event: E.OnOptionClicked): Next<M, F> = dispatch(
        setOf(
            when (event.option) {
                ProfileOption.PAYMENT_METHOD -> F.GoToPaymentMethod
                ProfileOption.SECURITY_SETTINGS -> F.GoToSettings(SettingsSection.SECURITY)
                ProfileOption.PREFERENCES -> F.GoToSettings(SettingsSection.PREFERENCES)
            }
        )
    )

    override fun onProfileDataLoaded(model: M, event: E.OnProfileDataLoaded): Next<M, F> = next(
        model.copy(
            profile = event.profile,
            isLoading = false
        ),
        setOf(F.LoadOptions(event.profile))
    )

    override fun onProfileDataLoadFailed(model: M, event: E.OnProfileDataLoadFailed): Next<M, F>  {
        val newModel = model.copy(isLoading = false)
        val effects = when (event.message) {
            null -> setOf(F.LoadOptions(null))
            else -> setOf(F.ShowFabriikToast(event.message), F.LoadOptions(null))
        }
        return next(newModel, effects)
    }
}
