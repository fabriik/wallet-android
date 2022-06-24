package com.breadwallet.ui.profile

import com.spotify.mobius.Next

interface ProfileScreenUpdateSpec {
    fun patch(model: ProfileScreen.M, event: ProfileScreen.E): Next<ProfileScreen.M, ProfileScreen.F> = when (event) {
        ProfileScreen.E.OnCloseClicked -> onCloseClicked(model)
        is ProfileScreen.E.OnOptionClicked -> onOptionClicked(model, event)
        is ProfileScreen.E.OnOptionsLoaded -> onOptionsLoaded(model, event)
        is ProfileScreen.E.OnProfileDataLoaded -> onProfileDataLoaded(model, event)
        is ProfileScreen.E.OnProfileDataLoadFailed -> onProfileDataLoadFailed(model, event)
        is ProfileScreen.E.OnChangeEmailClicked -> onChangeEmailClicked(model)
        is ProfileScreen.E.OnVerifyProfileClicked -> onVerifyProfileClicked(model)
        is ProfileScreen.E.OnUpgradeLimitsClicked -> onUpgradeLimitsClicked(model)
        is ProfileScreen.E.OnVerificationMoreInfoClicked -> onVerificationMoreInfoClicked(model)
        is ProfileScreen.E.OnVerificationDeclinedInfoClicked -> onVerificationDeclinedInfoClicked(model)
    }

    fun onCloseClicked(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onVerifyProfileClicked(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onUpgradeLimitsClicked(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onVerificationMoreInfoClicked(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onVerificationDeclinedInfoClicked(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onChangeEmailClicked(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onOptionClicked(model: ProfileScreen.M, event: ProfileScreen.E.OnOptionClicked): Next<ProfileScreen.M, ProfileScreen.F>

    fun onOptionsLoaded(model: ProfileScreen.M, event: ProfileScreen.E.OnOptionsLoaded): Next<ProfileScreen.M, ProfileScreen.F>

    fun onProfileDataLoaded(model: ProfileScreen.M, event: ProfileScreen.E.OnProfileDataLoaded): Next<ProfileScreen.M, ProfileScreen.F>

    fun onProfileDataLoadFailed(model: ProfileScreen.M, event: ProfileScreen.E.OnProfileDataLoadFailed): Next<ProfileScreen.M, ProfileScreen.F>
}