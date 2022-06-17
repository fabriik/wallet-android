/**
 * BreadWallet
 *
 * Created by Pablo Budelli <pablo.budelli@breadwallet.com> on 9/23/19.
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
package com.breadwallet.ui.pin

import com.breadwallet.tools.security.BrdUserManager
import com.breadwallet.tools.util.EventUtils
import com.breadwallet.ui.pin.InputPin.E
import com.breadwallet.ui.pin.InputPin.F
import com.fabriik.common.data.Status
import com.fabriik.registration.data.RegistrationApi
import com.fabriik.registration.data.responses.AssociateNewDeviceStatus
import com.fabriik.registration.utils.RegistrationUtils
import com.platform.tools.Session
import com.platform.tools.SessionState
import com.platform.tools.TokenHolder
import drewcarlson.mobius.flow.subtypeEffectHandler
import kotlinx.coroutines.flow.mapLatest
import java.util.*

fun createInputPinHandler(
    userManager: BrdUserManager,
    registrationApi: RegistrationApi
) = subtypeEffectHandler<F, E> {

    val registrationUtils = RegistrationUtils(userManager)

    addFunction<F.SetupPin> { effect ->
        try {
            userManager.configurePinCode(effect.pin)
            E.OnPinSaved
        } catch (e: Exception) {
            E.OnPinSaveFailed
        }
    }

    addFunction<F.CheckIfPinExists> {
        E.OnPinCheck(userManager.hasPinCode())
    }

    addFunction<F.ContinueWithFlow> {
        E.OnContinueToNextStep
    }

    addConsumer<F.TrackEvent> { (event) ->
        EventUtils.pushEvent(event)
    }

    addTransformer<F.AssociateNewDevice> { effect ->
        effect
            .mapLatest {
                val token = TokenHolder.retrieveToken() ?: return@mapLatest E.OnContinueToNextStep
                val nonce = UUID.randomUUID().toString()

                val response = registrationApi.associateNewDevice(
                    nonce,
                    token,
                    registrationUtils.getAssociateRequestHeaders(
                        salt = nonce,
                        token = token
                    )
                )

                when (response.status) {
                    Status.SUCCESS -> {
                        val status = response.data?.status
                        val email = response.data?.email
                        val sessionKey = response.data?.sessionKey

                        if (status == null || email.isNullOrBlank() || sessionKey.isNullOrBlank()) {
                            return@mapLatest E.OnContinueToNextStep
                        }

                        when (status) {
                            AssociateNewDeviceStatus.SENT -> {
                                userManager.putSession(
                                    Session(
                                        key = sessionKey,
                                        state = SessionState.CREATED
                                    )
                                )
                                E.OnVerifyEmailRequested(email)
                            }
                            else -> E.OnContinueToNextStep
                        }
                    }
                    Status.ERROR -> E.OnContinueToNextStep
                }
            }
    }
}

