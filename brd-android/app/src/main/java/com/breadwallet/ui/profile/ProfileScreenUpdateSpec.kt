/**
 * BreadWallet
 *
 * Created by Drew Carlson <drew.carlson@breadwallet.com> on 8/14/20.
 * Copyright (c) 2020 breadwallet LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.breadwallet.ui.profile

import com.spotify.mobius.Next

interface ProfileScreenUpdateSpec {
    fun patch(model: ProfileScreen.M, event: ProfileScreen.E): Next<ProfileScreen.M, ProfileScreen.F> = when (event) {
        ProfileScreen.E.OnBackClicked -> onBackClicked(model)
        ProfileScreen.E.OnCloseClicked -> onCloseClicked(model)
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

    }

    fun onBackClicked(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onCloseClicked(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onAuthenticated(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onWalletsUpdated(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun showHiddenOptions(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onCloseHiddenMenu(model: ProfileScreen.M): Next<ProfileScreen.M, ProfileScreen.F>

    fun onLinkScanned(model: ProfileScreen.M, event: ProfileScreen.E.OnLinkScanned): Next<ProfileScreen.M, ProfileScreen.F>

    fun onOptionClicked(model: ProfileScreen.M, event: ProfileScreen.E.OnOptionClicked): Next<ProfileScreen.M, ProfileScreen.F>

    fun onOptionsLoaded(model: ProfileScreen.M, event: ProfileScreen.E.OnOptionsLoaded): Next<ProfileScreen.M, ProfileScreen.F>

    fun showPhrase(model: ProfileScreen.M, event: ProfileScreen.E.ShowPhrase): Next<ProfileScreen.M, ProfileScreen.F>

    fun setApiServer(model: ProfileScreen.M, event: ProfileScreen.E.SetApiServer): Next<ProfileScreen.M, ProfileScreen.F>

    fun setPlatformDebugUrl(model: ProfileScreen.M, event: ProfileScreen.E.SetPlatformDebugUrl): Next<ProfileScreen.M, ProfileScreen.F>

    fun setPlatformBundle(model: ProfileScreen.M, event: ProfileScreen.E.SetPlatformBundle): Next<ProfileScreen.M, ProfileScreen.F>

    fun setTokenBundle(model: ProfileScreen.M, event: ProfileScreen.E.SetTokenBundle): Next<ProfileScreen.M, ProfileScreen.F>

    fun onATMMapClicked(model: ProfileScreen.M, event: ProfileScreen.E.OnATMMapClicked): Next<ProfileScreen.M, ProfileScreen.F>

    fun onExportTransactionsConfirmed(model: ProfileScreen.M) : Next<ProfileScreen.M, ProfileScreen.F>

    fun onTransactionsExportFileGenerated(model: ProfileScreen.M, event: ProfileScreen.E.OnTransactionsExportFileGenerated) : Next<ProfileScreen.M, ProfileScreen.F>
}