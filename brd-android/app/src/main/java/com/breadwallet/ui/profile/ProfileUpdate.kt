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
        dispatch(setOf(F.GoToKyc(model.profile!!)))

    override fun onUpgradeLimitsClicked(model: M): Next<M, F> =
        dispatch(setOf(F.GoToKyc(model.profile!!)))

    override fun onVerificationMoreInfoClicked(model: M): Next<M, F> =
        dispatch(setOf(F.GoToKyc(model.profile!!))) // TODO: open dialog instead

    override fun onVerificationDeclinedInfoClicked(model: M): Next<M, F> =
        dispatch(setOf(F.GoToKyc(model.profile!!))) // TODO: open dialog instead

    override fun onChangeEmailClicked(model: M): Next<M, F> =
        dispatch(emptySet()) //todo: call registration flow

    override fun onOptionsLoaded(
        model: M,
        event: E.OnOptionsLoaded
    ): Next<M, F> = next(model.copy(items = event.options))

    override fun onOptionClicked(model: M, event: E.OnOptionClicked): Next<M, F> = dispatch(
        setOf(
            when (event.option) {
                ProfileOption.SECURITY_SETTINGS -> F.GoToSettings(SettingsSection.SECURITY)
                ProfileOption.PREFERENCES -> F.GoToSettings(SettingsSection.PREFERENCES)
            }
        )
    )

    override fun onProfileDataLoaded(model: M, event: E.OnProfileDataLoaded): Next<M, F> =
        next(model.copy(profile = event.profile))
}
