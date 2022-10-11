package com.fabriik.buy.ui.features

import android.app.Application
import com.breadwallet.breadbox.BreadBox
import com.breadwallet.platform.interfaces.AccountMetaDataProvider
import com.fabriik.trade.data.SwapTransactionsFetcher
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton
import org.mockito.Mockito

class TestFabriikApplication : Application(), KodeinAware {

    override val kodein by Kodein.lazy {
        bind<BreadBox>() with singleton {
            Mockito.mock(BreadBox::class.java)
        }

        bind<SwapTransactionsFetcher>() with singleton {
            Mockito.mock(SwapTransactionsFetcher::class.java)
        }

        bind<AccountMetaDataProvider>() with singleton {
            Mockito.mock(AccountMetaDataProvider::class.java)
        }
    }
}