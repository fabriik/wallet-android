package com.fabriik.trade.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.fabriik.trade.databinding.FragmentConfirmationDialogBinding

class ConfirmationDialog : DialogFragment() {

    companion object {
        const val CONFIRMATION_TAG = "Confirmation_dialog"
    }

    lateinit var binding: FragmentConfirmationDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfirmationDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

}