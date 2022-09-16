package com.fabriik.kyc.ui.features.accountverification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.common.utils.FabriikToastUtil
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentAccountVerificationBinding
import com.fabriik.kyc.ui.customview.AccountVerificationStatusView
import com.fabriik.kyc.ui.customview.CheckedTextView
import com.fabriik.common.ui.dialog.InfoDialog
import com.fabriik.common.ui.dialog.InfoDialogArgs
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
            when (state) {
                is AccountVerificationContract.State.Content -> {
                    // level 1 configuration
                    tvLevel1Tag.isEnabled = state.level1State.isEnabled
                    tvLevel1CheckedItem1.setStateIcon(state.level1State.statusState)
                    setStatusState(tvLevel1Status, state.level1State.statusState)

                    // level 2 configuration
                    tvLevel2TagSwap.isEnabled = state.level2State.isEnabled
                    tvLevel2TagBuy.isEnabled = state.level2State.isEnabled
                    setStatusState(tvLevel2Status, state.level2State.statusState)

                    tvLevel2CheckedItem1.setStateIcon(state.level2State.statusState)
                    tvLevel2CheckedItem1.isVisible = state.level2State.verificationError == null

                    tvLevel2CheckedItemError.setStateIcon(state.level2State.statusState)
                    tvLevel2CheckedItemError.setContent(state.level2State.verificationError)
                    tvLevel2CheckedItemError.isVisible = state.level2State.verificationError != null
                }
            }

            accountLayout.isInvisible = state is AccountVerificationContract.State.Empty
            loadingIndicator.isVisible = when (state) {
                is AccountVerificationContract.State.Empty -> state.isLoading
                else -> false
            }
        }
    }

    override fun handleEffect(effect: AccountVerificationContract.Effect) {
        when (effect) {
            is AccountVerificationContract.Effect.Back,
            AccountVerificationContract.Effect.Dismiss ->
                requireActivity().finish()

            is AccountVerificationContract.Effect.Info ->
                showInfoDialog()

            is AccountVerificationContract.Effect.GoToKycLevel1 ->
                findNavController().navigate(
                    AccountVerificationFragmentDirections.actionKycLevel1()
                )

            is AccountVerificationContract.Effect.GoToKycLevel2 ->
                findNavController().navigate(
                    AccountVerificationFragmentDirections.actionKycLevel2()
                )

            is AccountVerificationContract.Effect.ShowToast ->
                FabriikToastUtil.showInfo(
                    parentView = binding.root,
                    message = effect.message
                )

            AccountVerificationContract.Effect.ShowLevel1ChangeConfirmationDialog -> TODO()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.updateProfile()
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

    private fun showInfoDialog() {
        val fm = requireActivity().supportFragmentManager
        val infoArgs = InfoDialogArgs(
            title = R.string.AccountVerification_InfoView_title,
            description = R.string.AccountVerification_InfoVIew_description,
        )
        InfoDialog(infoArgs).show(fm, "info_dialog")
    }
}