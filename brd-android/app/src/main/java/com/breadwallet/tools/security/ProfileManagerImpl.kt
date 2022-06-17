package com.breadwallet.tools.security

import com.fabriik.common.data.model.Profile
import com.fabriik.registration.data.RegistrationApi
import com.platform.tools.SessionHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProfileManagerImpl(
    private val userManager: BrdUserManager,
    private val registrationApi: RegistrationApi
) : ProfileManager {

    override fun getProfile() = userManager.getProfile()

    override fun updateProfile() : Flow<Profile?> = flow {
        if (SessionHolder.isDefaultSession()) {
            emit(null)
            return@flow
        }

        val response = registrationApi.getProfile()
        val profile = response.data

        if (profile != null) {
            userManager.putProfile(profile)
        }
        emit(profile)
    }
}