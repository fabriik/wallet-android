package com.breadwallet.app

import org.kodein.di.DKodein

object KodeinProvider {

    private lateinit var kodein: DKodein

    fun provideKodein(kodein: DKodein) {
        this.kodein = kodein
    }

    @JvmStatic
    fun get() = kodein
}