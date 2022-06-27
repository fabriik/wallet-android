package com.breadwallet.util

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.bluelinelabs.conductor.Router
import com.breadwallet.ui.BaseController
import com.breadwallet.ui.navigation.fragmentManager
import com.fabriik.common.ui.dialog.FabriikGenericDialog
import com.fabriik.common.ui.dialog.FabriikGenericDialogArgs

fun BaseController.registerForGenericDialogResult(requestKey: String, callback: (String?, Bundle) -> Unit) {
    router.fragmentManager()?.setFragmentResultListener(requestKey, activity as LifecycleOwner) { _, bundle ->
        val resultKey = bundle.getString(FabriikGenericDialog.EXTRA_RESULT)
        callback(resultKey, bundle)
    }
}

fun Router.showFabriikGenericDialog(args: FabriikGenericDialogArgs) {
    fragmentManager()?.let { fm ->
        FabriikGenericDialog.newInstance(args)
            .show(fm)
    }
}