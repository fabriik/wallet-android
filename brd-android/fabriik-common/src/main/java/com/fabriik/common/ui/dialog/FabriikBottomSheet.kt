package com.fabriik.common.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.fabriik.common.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class FabriikBottomSheet<Binding: ViewBinding> : BottomSheetDialogFragment() {

    protected lateinit var binding: Binding

    override fun getTheme() = R.style.FabriikBottomSheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(BOTTOM_SHEET_ID)
                ?: return@setOnShowListener

            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = createBinding(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheet()
    }

    fun show(fragmentManager: FragmentManager?) {
        if (fragmentManager == null) return
        show(fragmentManager, this.javaClass.name + System.currentTimeMillis())
    }

    protected fun dismissWithResult(requestKey: String, resultKey: String, data: Bundle? = null) {
        /*getParentFragmentManager().setFragmentResult(
            requestKey,
            resultToBundle(resultKey, dataToReturn)
        )*/
        dismissAllowingStateLoss()
    }

    protected fun resultToBundle(resultKey: String, dataToReturn: Bundle?): Bundle {
        val bundle = Bundle()
        bundle.putString(EXTRA_RESULT_KEY, resultKey)
        dataToReturn?.let { bundle.putParcelable(EXTRA_DATA_TO_RETURN, it) }
        return bundle
    }

    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?, attach: Boolean): Binding

    abstract fun setupBottomSheet()

    companion object {
        const val EXTRA_RESULT_KEY = "extra_result_key"
        const val EXTRA_DATA_TO_RETURN = "extra_data_to_return"

        @IdRes
        private val BOTTOM_SHEET_ID = R.id.design_bottom_sheet
    }
}