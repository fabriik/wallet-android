package com.breadwallet.tools.security

import android.util.Log
import com.fabriik.common.data.Status
import com.fabriik.common.data.model.Profile
import com.fabriik.registration.data.RegistrationApi
import com.platform.tools.SessionHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileManagerImpl(
    private val scope: CoroutineScope,
    private val userManager: BrdUserManager,
    private val registrationApi: RegistrationApi
) : ProfileManager {

    private val changeEventChannel = BroadcastChannel<Unit>(Channel.CONFLATED)

    override fun getProfile() = userManager.getProfile()

    override fun profileChanges(): Flow<Profile?> = changeEventChannel.asFlow()
        .onStart { emit(Unit) }
        .map { getProfile() }
        .distinctUntilChanged()
        .flowOn(Dispatchers.Default)

    override fun updateProfile() {
        if (SessionHolder.isDefaultSession()) {
            return
        }

        scope.launch(Dispatchers.IO) {
            val response = registrationApi.getProfile()
            when (response.status) {
                Status.SUCCESS -> {
                    userManager.putProfile(response.data)
                    changeEventChannel.offer(Unit)
                }
                Status.ERROR -> {
                    Log.d("ProfileManager", "Update profile failed: ${response.message}")
                }
            }
        }
    }
}