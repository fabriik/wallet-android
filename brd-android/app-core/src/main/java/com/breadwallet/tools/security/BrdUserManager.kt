/**
 * BreadWallet
 *
 * Created by Drew Carlson <drew.carlson@breadwallet.com> on 8/12/19.
 * Copyright (c) 2019 breadwallet LLC
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
package com.breadwallet.tools.security

import com.breadwallet.crypto.Account
import com.fabriik.common.data.model.Profile
import com.platform.tools.Session
import kotlinx.coroutines.flow.Flow

/** Manages creation, recovery, and access to an [Account]. */
@Suppress("TooManyFunctions")
interface BrdUserManager {
    suspend fun setupWithGeneratedPhrase(): SetupResult
    suspend fun setupWithPhrase(phrase: ByteArray): SetupResult
    suspend fun migrateKeystoreData(): Boolean

    suspend fun checkAccountInvalidated()

    fun isInitialized(): Boolean
    fun getState(): BrdUserState
    fun stateChanges(disabledUpdates: Boolean = false): Flow<BrdUserState>

    fun isMigrationRequired(): Boolean

    suspend fun getPhrase(): ByteArray?
    fun getAccount(): Account?
    fun updateAccount(accountBytes: ByteArray)
    fun getAuthKey(): ByteArray?

    suspend fun configurePinCode(pinCode: String)
    suspend fun clearPinCode(phrase: ByteArray)
    fun getRemainingPinAttempts(): Int
    fun verifyPinCode(pinCode: String, walletLockable: Boolean): Boolean
    fun hasPinCode(): Boolean
    fun pinCodeNeedsUpgrade(): Boolean

    fun lock()
    fun unlock()

    fun getToken(): String?
    fun putToken(token: String)
    fun removeToken()

    fun getProfile(): Profile?
    fun putProfile(profile: Profile?)

    fun getSession(): Session?
    fun putSession(session: Session)
    fun removeSession()

    fun getBdbJwt(): String?
    fun putBdbJwt(jwt: String, exp: Long)

    fun showVerifyPrompt(): Boolean
    fun updateVerifyPrompt(showVerifyPrompt: Boolean)

    fun onActivityResult(requestCode: Int, resultCode: Int)

    companion object {
        const val PIN_LENGTH = 6
        const val LEGACY_PIN_LENGTH = 4
    }
}