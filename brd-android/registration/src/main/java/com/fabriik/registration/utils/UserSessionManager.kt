package com.fabriik.registration.utils

import android.content.Context
import com.fabriik.common.data.FabriikApiResponseError
import kotlinx.coroutines.CoroutineScope

interface UserSessionManager {

    fun onSessionExpired(context: Context, scope: CoroutineScope)

    fun isSessionExpiredError(error: FabriikApiResponseError) : Boolean
}