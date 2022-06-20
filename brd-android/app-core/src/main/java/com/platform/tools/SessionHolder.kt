package com.platform.tools

import android.content.Context
import com.breadwallet.tools.security.BrdUserManager
import com.fabriik.common.BuildConfig
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

object SessionHolder : KodeinAware {

    private val defaultSession = Session(
        key = BuildConfig.DEFAULT_FABRIIK_CLIENT_TOKEN,
        state = SessionState.DEFAULT
    )

    private var mApiSession: Session? = null
    private lateinit var context: Context

    fun provideContext(context: Context) {
        this.context = context
    }

    override val kodein by closestKodein { context }
    private val userManager by instance<BrdUserManager>()

    @Synchronized
    fun getSession() = userManager.getSession() ?: defaultSession

    @Synchronized
    fun getSessionKey() = getSession().key

    @Synchronized
    fun updateSession(sessionKey: String, state: SessionState): Session? {
        if (mApiSession == null || mApiSession!!.key != sessionKey || mApiSession!!.state != state) {
            mApiSession = Session(
                key = sessionKey,
                state = state
            )
            userManager.putSession(mApiSession!!)
        }
        return mApiSession
    }

    @Synchronized
    fun isDefaultSession() = getSession().isDefaultSession()

    @Synchronized
    fun isUserSessionVerified() : Boolean {
        val session = getSession()
        return !session.isDefaultSession() && session.state == SessionState.VERIFIED
    }

    @Synchronized
    fun clear() {
        userManager.removeSession()
        mApiSession = null
    }
}

data class Session(
    val state: SessionState,
    val key: String
) {
    @Synchronized
    fun isDefaultSession() = state == SessionState.DEFAULT
}

enum class SessionState(val id: String) {
    CREATED("created"),
    VERIFIED("verified"),
    EXPIRED("expired"),
    DEFAULT("default")
}