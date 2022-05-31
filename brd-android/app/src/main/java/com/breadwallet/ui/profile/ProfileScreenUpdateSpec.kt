package com.breadwallet.ui.profile

import com.spotify.mobius.Next

interface ProfileScreenUpdateSpec {
    fun patch(model: ProfileScreen.M, event: ProfileScreen.E): Next<ProfileScreen.M, ProfileScreen.F> = when (event) {
        ProfileScreen.E.OnCloseClicked -> onCloseClicked(model)
        is ProfileScreen.E.OnOptionClicked -> onOptionClicked(model, event)
        is ProfileScreen.E.OnOptionsLoaded -> onOptionsLoaded(model, event)
    }

    fun onCloseClicked(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onOptionClicked(model: ProfileScreen.M, event: ProfileScreen.E.OnOptionClicked): Next<ProfileScreen.M, ProfileScreen.F>

    fun onOptionsLoaded(model: ProfileScreen.M, event: ProfileScreen.E.OnOptionsLoaded): Next<ProfileScreen.M, ProfileScreen.F>
}