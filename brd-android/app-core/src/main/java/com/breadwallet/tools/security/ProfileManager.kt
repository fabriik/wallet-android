package com.breadwallet.tools.security

import com.fabriik.common.data.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileManager {

    fun getProfile() : Profile?

    fun profileChanges() : Flow<Profile?>

    fun updateProfile()
}