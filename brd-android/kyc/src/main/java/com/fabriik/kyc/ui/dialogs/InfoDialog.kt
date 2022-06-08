package com.fabriik.kyc.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import com.fabriik.kyc.databinding.FragmentInfoDialogBinding

class InfoDialog(
    val args: InfoDialogArgs
) : DialogFragment() {

    lateinit var binding: FragmentInfoDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoDialogBinding.inflate(inflater, container, false)

        binding.tvTitle.text = context?.getString(args.title)
        binding.tvDescription.text = context?.getString(args.description)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.btnDismiss.setOnClickListener {
            dialog?.dismiss()
        }
    }
}

data class InfoDialogArgs(
    @StringRes val title: Int,
    @StringRes val description: Int,
)