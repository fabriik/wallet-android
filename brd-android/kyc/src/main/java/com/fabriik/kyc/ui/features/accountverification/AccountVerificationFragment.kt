package com.fabriik.kyc.ui.features.accountverification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListPopupWindow
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentAccountVerificationBinding
import com.fabriik.kyc.ui.dialogs.InfoPopup
import kotlinx.coroutines.flow.collect

class AccountVerificationFragment : Fragment(),
    FabriikView<AccountVerificationContract.State, AccountVerificationContract.Effect> {

    private lateinit var binding: FragmentAccountVerificationBinding
    private val viewModel: AccountVerificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_account_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountVerificationBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(AccountVerificationContract.Event.BackClicked)
            }

            tvInfo.setOnClickListener {
                viewModel.setEvent(AccountVerificationContract.Event.InfoClicked)
            }

            cvBasic.setOnClickListener {
                viewModel.setEvent(AccountVerificationContract.Event.BasicClicked)
            }

            cvUnlimited.setOnClickListener {
                viewModel.setEvent(AccountVerificationContract.Event.UnlimitedClicked)
            }
        }

        // collect UI state
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                render(it)
            }
        }

        // collect UI effect
        lifecycleScope.launchWhenStarted {
            viewModel.effect.collect {
                handleEffect(it)
            }
        }
    }

    override fun render(state: AccountVerificationContract.State) {
        with(binding) {
            cvBasic.isEnabled = state.basicBoxEnabled
            cvUnlimited.isEnabled = state.unlimitedBoxEnabled
        }
    }

    override fun handleEffect(effect: AccountVerificationContract.Effect) {
        when (effect) {
            is AccountVerificationContract.Effect.GoBack ->
                requireActivity().finish()

            is AccountVerificationContract.Effect.GoToPersonalInfo ->
                findNavController().navigate(
                    AccountVerificationFragmentDirections.actionToPersonalInfo()
                )

            is AccountVerificationContract.Effect.GoToProofOfIdentity ->
                findNavController().navigate(
                    AccountVerificationFragmentDirections.actionToProofOfIdentity()
                )

            is AccountVerificationContract.Effect.ShowInfo ->
                InfoPopup.showPopupWindow(
                    anchorView = binding.toolbar,
                    parentView = binding.root,
                    title = effect.title,
                    description = effect.description,
                )
        }
    }

    private fun showPopup(message: String) {


        val context = requireContext()

        val tv = TextView(context)
        tv.text = message

        val popupWindow = PopupWindow(context)
        popupWindow.width = 300
        popupWindow.contentView = tv
        popupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_info_prompt)
        )
        // popupWindow.promptPosition = ListPopupWindow.POSITION_PROMPT_BELOW
        popupWindow.showAsDropDown(binding.toolbar)
    }
}