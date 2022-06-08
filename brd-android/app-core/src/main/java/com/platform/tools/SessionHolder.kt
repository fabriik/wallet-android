package com.platform.tools

import android.content.Context
import com.breadwallet.tools.security.BrdUserManager
import com.fabriik.common.BuildConfig
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

object SessionHolder : KodeinAware {

    private var mApiSession: String? = null
    private lateinit var context: Context

    fun provideContext(context: Context) {
        this.context = context
    }

    override val kodein by closestKodein { context }
    private val userManager by instance<BrdUserManager>()

    @Synchronized
    fun retrieveSession() = userManager.getSession()
        ?: BuildConfig.DEFAULT_FABRIIK_CLIENT_TOKEN

    @Synchronized
    fun updateSession(session: String): String? {
        if (mApiSession == null || mApiSession != session) {
            mApiSession = session
            userManager.putSession(session)
        }
        return mApiSession
    }

    @Synchronized
    fun clear() {
        userManager.removeSession()
        mApiSession = null
    }
}
