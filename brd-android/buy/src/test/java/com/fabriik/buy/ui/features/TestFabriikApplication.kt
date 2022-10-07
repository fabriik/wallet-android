package com.fabriik.buy.ui.features

import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

class TestFabriikApplication : Application(), KodeinAware {

    override val kodein by Kodein.lazy {

    }
}