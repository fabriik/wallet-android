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

    override fun getTheme() = R.style.FabriikGenericDialogStyle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_fabriik_generic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogFabriikGenericBinding.bind(view)
        args = arguments?.getParcelable(EXTRA_ARGS) as FabriikGenericDialogArgs?
            ?: throw IllegalStateException()

        with(binding) {

            // setup views
            val title = if (args.titleRes != null) getString(args.titleRes!!) else args.title
            tvTitle.text = title
            tvTitle.isVisible = title != null

            val description = if (args.descriptionRes != null) getString(args.descriptionRes!!) else args.description
            tvDescription.text = description
            tvDescription.isVisible = description != null

            val positiveText = if (args.positive?.titleRes != null) getString(args.positive?.titleRes!!) else args.positive?.title
            btnPositive.text = positiveText
            btnPositive.isVisible = args.positive != null
            args.positive?.icon?.let { btnPositive.setIconResource(it) }

            val negativeText = if (args.negative?.titleRes != null) getString(args.negative?.titleRes!!) else args.negative?.title
            btnNegative.text = negativeText
            btnNegative.isVisible = args.negative != null
            args.negative?.icon?.let { btnNegative.setIconResource(it) }

            // setup listeners
            btnDismiss.setOnClickListener {
                notifyListeners(RESULT_KEY_DISMISSED)
            }

            btnPositive.setOnClickListener {
                val resultKey = args.positive?.resultKey ?: return@setOnClickListener
                notifyListeners(resultKey)
            }

            btnNegative.setOnClickListener {
                val resultKey = args.negative?.resultKey ?: return@setOnClickListener
                notifyListeners(resultKey)
            }
        }
    }

    fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    private fun notifyListeners(result: String) {
        dismissAllowingStateLoss()

        requireActivity().supportFragmentManager.setFragmentResult(
            args.requestKey, bundleOf(EXTRA_RESULT to result)
        )
    }

    companion object {
        private const val TAG = "Fabriik-Generic-Dialog"
        private const val EXTRA_ARGS = "args"
        const val EXTRA_RESULT = "result"
        private const val RESULT_KEY_DISMISSED = "result_dismissed"

        fun newInstance(args: FabriikGenericDialogArgs): FabriikGenericDialog {
            val dialog = FabriikGenericDialog()
            dialog.isCancelable = false
            dialog.arguments = bundleOf(EXTRA_ARGS to args)
            return dialog
        }
    }
}