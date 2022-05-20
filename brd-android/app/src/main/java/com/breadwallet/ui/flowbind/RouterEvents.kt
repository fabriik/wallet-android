package com.breadwallet.ui.flowbind

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.bluelinelabs.conductor.Router
import com.breadwallet.ui.navigation.fragmentManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

fun Router.dialogResult(requestKey: String): Flow<Bundle> =
    callbackFlow {
        val lifecycleOwner = activity as LifecycleOwner?
        lifecycleOwner?.let {
            fragmentManager()?.setFragmentResultListener(requestKey, it) { _, bundle ->
                offer(bundle)
            }
        }
        awaitClose { fragmentManager()?.clearFragmentResultListener(requestKey) }
    }.flowOn(Dispatchers.Main)
