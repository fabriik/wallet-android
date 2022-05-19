package com.fabriik.common.ui.dialog

import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import java.lang.IllegalStateException

class FabriikGenericDialog : DialogFragment() {

    private val args = arguments?.getParcelable(EXTRA_ARGS) as FabriikGenericDialogArgs?
        ?: throw IllegalStateException()

    fun show(manager: FragmentManager) {
        show(manager, TAG.format(args.requestKey))
    }

    companion object {
        private const val TAG = "Fabriik-Generic-Dialog-%s"
        private const val EXTRA_ARGS = "args"

        fun newInstance(args: FabriikGenericDialogArgs) = FabriikGenericDialog().apply {
            this.arguments = bundleOf(
                EXTRA_ARGS to args
            )
        }
    }
}