package com.breadwallet.ui.profile

import com.spotify.mobius.Next
import drewcarlson.mobius.flow.dispatch
import drewcarlson.mobius.flow.next

interface ProfileScreenUpdateSpec {
    fun patch(model: ProfileScreen.M, event: ProfileScreen.E): Next<ProfileScreen.M, ProfileScreen.F> = when (event) {
        else -> dispatch(emptySet())
        /*ProfileScreen.E.OnBackClicked -> onBackClicked(model)
        ProfileScreen.E.OnCloseClicked -> onCloseClicked(model)
        ProfileScreen.E.OnVerifyProfileClicked -> onVerifyProfileClicked(model)
        ProfileScreen.E.OnProfileVerificationInfoClicked -> onProfileVerificationInfoClicked(model)
        ProfileScreen.E.OnAuthenticated -> onAuthenticated(model)
        ProfileScreen.E.OnWalletsUpdated -> onWalletsUpdated(model)
        ProfileScreen.E.ShowHiddenOptions -> showHiddenOptions(model)
        ProfileScreen.E.OnCloseHiddenMenu -> onCloseHiddenMenu(model)
        ProfileScreen.E.OnExportTransactionsConfirmed  -> onExportTransactionsConfirmed(model)
        is ProfileScreen.E.OnLinkScanned -> onLinkScanned(model, event)
        is ProfileScreen.E.OnOptionClicked -> onOptionClicked(model, event)
        is ProfileScreen.E.OnOptionsLoaded -> onOptionsLoaded(model, event)
        is ProfileScreen.E.ShowPhrase -> showPhrase(model, event)
        is ProfileScreen.E.SetApiServer -> setApiServer(model, event)
        is ProfileScreen.E.SetPlatformDebugUrl -> setPlatformDebugUrl(model, event)
        is ProfileScreen.E.SetPlatformBundle -> setPlatformBundle(model, event)
        is ProfileScreen.E.SetTokenBundle -> setTokenBundle(model, event)
        is ProfileScreen.E.OnATMMapClicked -> onATMMapClicked(model, event)
        is ProfileScreen.E.OnTransactionsExportFileGenerated -> onTransactionsExportFileGenerated(model, event)
        is ProfileScreen.E.OnProfileVerificationInfoResult -> onProfileVerificationInfoResult(event)*/
    }
/*
    fun onBackClicked(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onCloseClicked(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onVerifyProfileClicked(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onProfileVerificationInfoClicked(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onAuthenticated(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onWalletsUpdated(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun showHiddenOptions(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onCloseHiddenMenu(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onLinkScanned(model: ProfileScreen.M, event: ProfileScreen.E.OnLinkScanned): Next<ProfileScreen.M, ProfileScreen.F>

    fun onProfileVerificationInfoResult(event: ProfileScreen.E.OnProfileVerificationInfoResult): Next<ProfileScreen.M, ProfileScreen.F>

    fun onOptionClicked(model: ProfileScreen.M, event: ProfileScreen.E.OnOptionClicked): Next<ProfileScreen.M, ProfileScreen.F>

    fun onOptionsLoaded(model: ProfileScreen.M, event: ProfileScreen.E.OnOptionsLoaded): Next<ProfileScreen.M, ProfileScreen.F>

    fun showPhrase(model: ProfileScreen.M, event: ProfileScreen.E.ShowPhrase): Next<ProfileScreen.M, ProfileScreen.F>

    fun setApiServer(model: ProfileScreen.M, event: ProfileScreen.E.SetApiServer): Next<ProfileScreen.M, ProfileScreen.F>

    fun setPlatformDebugUrl(model: ProfileScreen.M, event: ProfileScreen.E.SetPlatformDebugUrl): Next<ProfileScreen.M, ProfileScreen.F>

    fun setPlatformBundle(model: ProfileScreen.M, event: ProfileScreen.E.SetPlatformBundle): Next<ProfileScreen.M, ProfileScreen.F>

    fun setTokenBundle(model: ProfileScreen.M, event: ProfileScreen.E.SetTokenBundle): Next<ProfileScreen.M, ProfileScreen.F>

    fun onATMMapClicked(model: ProfileScreen.M, event: ProfileScreen.E.OnATMMapClicked): Next<ProfileScreen.M, ProfileScreen.F>

    fun onExportTransactionsConfirmed(model: ProfileScreen.M) : Next<ProfileScreen.M, ProfileScreen.F>

    fun onTransactionsExportFileGenerated(model: ProfileScreen.M, event: ProfileScreen.E.OnTransactionsExportFileGenerated) : Next<ProfileScreen.M, ProfileScreen.F>*/
}