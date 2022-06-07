package com.fabriik.kyc.ui.features.accountverification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentAccountVerificationBinding
import com.fabriik.kyc.ui.customview.AccountVerificationStatusView
import com.fabriik.kyc.ui.customview.CheckedTextView
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

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(AccountVerificationContract.Event.DismissClicked)
            }

            toolbar.setInfoButtonClickListener {
                viewModel.setEvent(AccountVerificationContract.Event.InfoClicked)
            }

            cvLevel1.setOnClickListener {
                viewModel.setEvent(AccountVerificationContract.Event.Level1Clicked)
            }

            cvLevel2.setOnClickListener {
                viewModel.setEvent(AccountVerificationContract.Event.Level2Clicked)
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
            // level 1 configuration
            cvLevel1.isEnabled = state.level1State.isEnabled
            tvLevel1Tag.isEnabled = state.level1State.isEnabled
            tvLevel1CheckedItem1.setStateIcon(state.level1State.statusState)
            setStatusState(tvLevel1Status, state.level1State.statusState)

            // level 2 configuration
            cvLevel2.isEnabled = state.level2State.isEnabled
            tvLevel2Tag.isEnabled = state.level2State.isEnabled
            setStatusState(tvLevel2Status, state.level2State.statusState)

            tvLevel2CheckedItem1.setStateIcon(state.level2State.statusState)
            tvLevel2CheckedItem1.isVisible = state.level2State.verificationError == null

            tvLevel2CheckedItem2.setStateIcon(state.level2State.statusState)
            tvLevel2CheckedItem2.isVisible = state.level2State.verificationError == null

            tvLevel2CheckedItemError.setStateIcon(state.level2State.statusState)
            tvLevel2CheckedItemError.setContent(state.level2State.verificationError)
            tvLevel2CheckedItemError.isVisible = state.level2State.verificationError != null
        }
    }

    override fun handleEffect(effect: AccountVerificationContract.Effect) {
        when (effect) {
            is AccountVerificationContract.Effect.Back ->
                requireActivity().finish()

            is AccountVerificationContract.Effect.Dismiss ->
                requireActivity().finish()

            is AccountVerificationContract.Effect.Info -> {
                // TODO - open dialog/snackabar with info
            }

            is AccountVerificationContract.Effect.GoToPersonalInfo ->
                findNavController().navigate(
                    AccountVerificationFragmentDirections.actionPersonalInfo()
                )

            /*

            is AccountVerificationContract.Effect.GoToProofOfIdentity ->
                findNavController().navigate(
                    AccountVerificationFragmentDirections.actionToProofOfIdentity()
                )*/
        }
    }

    private fun setStatusState(
        statusView: TextView,
        state: AccountVerificationStatusView.StatusViewState?
    ) {
        statusView.isVisible = state != null

        state ?: return

        statusView.setText(state.text)
        statusView.setTextColor(
            ContextCompat.getColor(
                requireContext(), state.textColor
            )
        )
        statusView.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(), state.backgroundTint
        )
    }

    private fun CheckedTextView.setStateIcon(status: AccountVerificationStatusView.StatusViewState?) {
        val iconId = when (status) {
            is AccountVerificationStatusView.StatusViewState.Verified ->
                R.drawable.ic_check_blue

            is AccountVerificationStatusView.StatusViewState.Resubmit,
            AccountVerificationStatusView.StatusViewState.Declined ->
                R.drawable.ic_check_error

            is AccountVerificationStatusView.StatusViewState.Pending, null ->
                R.drawable.ic_check_grey
        }

        setIcon(ContextCompat.getDrawable(context, iconId))
    }
}