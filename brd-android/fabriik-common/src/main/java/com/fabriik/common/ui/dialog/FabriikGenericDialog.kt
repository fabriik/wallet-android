package com.fabriik.common.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.fabriik.common.R
import com.fabriik.common.databinding.DialogFabriikGenericBinding
import java.lang.IllegalStateException

class FabriikGenericDialog : DialogFragment() {

    private lateinit var args: FabriikGenericDialogArgs
    private lateinit var binding: DialogFabriikGenericBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_fabriik_generic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogFabriikGenericBinding.bind(view)
        args = arguments?.getParcelable(EXTRA_ARGS) as FabriikGenericDialogArgs?
            ?: throw IllegalStateException()

        with(binding) {
            tvTitle.text = args.title
            tvTitle.isVisible = args.title != null

            tvDescription.text = args.description
            tvDescription.isVisible = args.description != null

            btnPositive.text = args.positive?.title
            btnPositive.isVisible = args.positive != null
            args.positive?.icon?.let { btnPositive.setIconResource(it) }

            btnNegative.text = args.negative?.title
            btnNegative.isVisible = args.negative != null
            args.negative?.icon?.let { btnNegative.setIconResource(it) }
        }
    }

    fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    companion object {
        private const val TAG = "Fabriik-Generic-Dialog"
        private const val EXTRA_ARGS = "args"

        fun newInstance(args: FabriikGenericDialogArgs): FabriikGenericDialog {
            val dialog = FabriikGenericDialog()
            dialog.arguments = bundleOf(EXTRA_ARGS to args)
            return dialog
        }
    }
}