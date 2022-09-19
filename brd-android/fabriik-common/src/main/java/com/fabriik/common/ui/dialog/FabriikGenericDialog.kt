package com.fabriik.common.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.fabriik.common.R
import com.fabriik.common.databinding.DialogFabriikGenericBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
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
            setupIcon(ivIcon)
            setupTitle(tvTitle)
            setupDescription(tvDescription)
            setupDismissButton(btnDismiss)
            setupPositiveButton(btnPositive)
            setupNegativeButton(btnNegative)
            setupInputTextField(inputTextField)
        }
    }

    fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    private fun setupIcon(view: ImageView) {
        args.iconRes?.let(view::setImageResource)
        view.isVisible = args.iconRes != null
    }

    private fun setupTitle(view: TextView) {
        val title = if (args.titleRes != null) getString(args.titleRes!!) else args.title
        view.text = title
        view.gravity = args.titleTextGravity
        view.isVisible = title != null
    }

    private fun setupDescription(view: TextView) {
        val description = if (args.descriptionRes != null) getString(args.descriptionRes!!) else args.description
        view.text = description
        view.isVisible = description != null
    }

    private fun setupPositiveButton(button: MaterialButton) {
        val positiveText = if (args.positive?.titleRes != null) getString(args.positive?.titleRes!!) else args.positive?.title
        button.text = positiveText
        button.isVisible = args.positive != null
        args.positive?.icon?.let { button.setIconResource(it) }

        button.setOnClickListener {
            val resultKey = args.positive?.resultKey ?: return@setOnClickListener
            notifyListeners(resultKey, binding.etDialog.text.toString())
        }
    }

    private fun setupNegativeButton(button: MaterialButton) {
        val negativeText = if (args.negative?.titleRes != null) getString(args.negative?.titleRes!!) else args.negative?.title
        button.text = negativeText
        button.isVisible = args.negative != null
        args.negative?.icon?.let { button.setIconResource(it) }

        button.setOnClickListener {
            val resultKey = args.negative?.resultKey ?: return@setOnClickListener
            notifyListeners(resultKey, null)
        }
    }

    private fun setupDismissButton(button: ImageButton) {
        button.isVisible = args.showDismissButton
        button.setOnClickListener {
            notifyListeners(RESULT_KEY_DISMISSED, null)
        }
    }

    private fun setupInputTextField(view: TextInputLayout) {
        val hintText = if (args.textInputHintRes != null) getString(args.textInputHintRes!!) else args.textInputHint
        view.isVisible = hintText != null
        view.hint = hintText
    }

    private fun notifyListeners(result: String, extraInput: String?) {
        dismissAllowingStateLoss()

        requireActivity().supportFragmentManager.setFragmentResult(
            args.requestKey, bundleOf(
                EXTRA_RESULT to result,
                EXTRA_TEXT_INPUT to extraInput
            )
        )
    }

    companion object {
        private const val TAG = "Fabriik-Generic-Dialog"
        private const val EXTRA_ARGS = "args"
        const val EXTRA_RESULT = "result"
        const val EXTRA_TEXT_INPUT = "input"
        private const val RESULT_KEY_DISMISSED = "result_dismissed"

        fun newInstance(args: FabriikGenericDialogArgs): FabriikGenericDialog {
            val dialog = FabriikGenericDialog()
            dialog.isCancelable = false
            dialog.arguments = bundleOf(EXTRA_ARGS to args)
            return dialog
        }
    }
}