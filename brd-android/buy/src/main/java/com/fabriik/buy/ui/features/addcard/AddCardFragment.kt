package com.fabriik.buy.ui.features.addcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.buy.R
import com.fabriik.buy.databinding.FragmentAddCardBinding
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.common.ui.dialog.InfoDialog
import com.fabriik.common.ui.dialog.InfoDialogArgs
import com.fabriik.common.utils.FabriikToastUtil
import kotlinx.coroutines.flow.collect

class AddCardFragment : Fragment(), FabriikView<AddCardContract.State, AddCardContract.Effect> {

    private lateinit var binding: FragmentAddCardBinding

    private val viewModel: AddCardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddCardBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(AddCardContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(AddCardContract.Event.DismissClicked)
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(AddCardContract.Event.ConfirmClicked)
            }

            tilSecurityCode.setEndIconOnClickListener {
                viewModel.setEvent(AddCardContract.Event.SecurityCodeInfoClicked)
            }

            etCardNumber.doAfterTextChanged {
                viewModel.setEvent(AddCardContract.Event.OnCardNumberChanged(it.toString()))
            }

            etDate.doAfterTextChanged {
                viewModel.setEvent(AddCardContract.Event.OnDateChanged(it.toString()))
            }

            etSecurityCode.doAfterTextChanged {
                viewModel.setEvent(AddCardContract.Event.OnSecurityCodeChanged(it.toString()))
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

    override fun render(state: AddCardContract.State) {
        with(binding) {
            etCardNumber.setText(state.cardNumber)
            etCardNumber.setSelection(state.cardNumber.length)

            etDate.setText(state.expiryDate)
            etDate.setSelection(state.expiryDate.length)

            btnConfirm.isEnabled = state.confirmButtonEnabled

            viewLoading.root.isVisible = state.loadingIndicatorVisible
        }
    }

    override fun handleEffect(effect: AddCardContract.Effect) {
        when (effect) {
            AddCardContract.Effect.Back ->
                findNavController().popBackStack()

            AddCardContract.Effect.Dismiss ->
                activity?.finish()

            AddCardContract.Effect.ShowCvvInfoDialog ->
                showCvvInfoDialog()

            is AddCardContract.Effect.BillingAddress ->
                findNavController().navigate(
                    AddCardFragmentDirections.actionBillingAddress(
                        effect.token, effect.flow
                    )
                )

            is AddCardContract.Effect.ShowError ->
                FabriikToastUtil.showError(
                    parentView = binding.root,
                    message = effect.message
                )
        }
    }

    private fun showCvvInfoDialog() {
        val fm = parentFragmentManager
        val args = InfoDialogArgs(
            image = R.drawable.ic_info_cvv,
            title = R.string.Buy_SecurityCode,
            description = R.string.Buy_SecurityCodePopup
        )

        InfoDialog(args).show(fm, InfoDialog.TAG)
    }
}

enum class AddCardFlow {
    BUY,
    PROFILE
}