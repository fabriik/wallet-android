package com.breadwallet.tools.security

import android.content.Context
import com.fabriik.common.data.Status
import com.fabriik.common.data.model.Profile
import com.fabriik.registration.data.RegistrationApi
import com.fabriik.registration.utils.UserSessionManager
import com.fabriik.registration.utils.UserSessionManagerImpl
import com.platform.tools.SessionHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProfileManagerImpl(
    private val context: Context,
    private val scope: CoroutineScope,
    private val userManager: BrdUserManager,
    private val registrationApi: RegistrationApi,
    private val userSessionManager: UserSessionManager
) : ProfileManager {

    override fun getProfile() = userManager.getProfile()

    override fun updateProfile() : Flow<Profile?> = flow {
        if (SessionHolder.isDefaultSession()) {
            emit(null)
            return@flow
        }

        val response = registrationApi.getProfile()
        when (response.status) {
            Status.SUCCESS -> {
                userManager.putProfile(response.data)
                emit(response.data)
            }
            Status.ERROR -> {
                if (userSessionManager.isSessionExpiredError(response.throwable)) {
                    userSessionManager.onSessionExpired(
                        context = context,
                        scope = scope
                    )
                }
            }
        }
    }
}