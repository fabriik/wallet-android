package com.fabriik.kyc.ui.features.accountverification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentAccountVerificationBinding
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
            // basic item configuration
            cvBasic.isEnabled = state.basicBoxEnabled
            cvBasic.elevation = if (state.basicBoxCompleted) 0f else cvBasic.elevation
            tvBasicTag.isActivated = state.basicBoxCompleted
            ivBasicCompleted.isVisible = state.basicBoxCompleted
            tvBasicCheckItem.isActivated = state.basicBoxCompleted

            // unlimited item configuration
            cvUnlimited.isEnabled = state.unlimitedBoxEnabled
            cvUnlimited.elevation = if (state.unlimitedBoxCompleted) 0f else cvUnlimited.elevation
            tvUnlimitedTag.isActivated = state.unlimitedBoxCompleted
            ivUnlimitedCompleted.isVisible = state.unlimitedBoxCompleted
            tvUnlimitedCheckItem1.isActivated = state.unlimitedBoxCompleted
            tvUnlimitedCheckItem2.isActivated = state.unlimitedBoxCompleted
            tvUnlimitedCheckItem3.isActivated = state.unlimitedBoxCompleted
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
        }
    }
}