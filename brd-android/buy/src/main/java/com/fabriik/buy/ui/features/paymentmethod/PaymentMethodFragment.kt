package com.fabriik.buy.ui.features.paymentmethod

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fabriik.buy.R
import com.fabriik.buy.databinding.FragmentPaymentMethodBinding
import com.fabriik.common.data.model.PaymentInstrument
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.common.ui.dialog.FabriikBottomSheet
import com.fabriik.common.ui.dialog.FabriikGenericDialog
import com.fabriik.common.utils.FabriikToastUtil
import kotlinx.coroutines.flow.collect
import kotlinx.parcelize.Parcelize

class PaymentMethodFragment : Fragment(),
    FabriikView<PaymentMethodContract.State, PaymentMethodContract.Effect> {

    private lateinit var binding: FragmentPaymentMethodBinding
    private val viewModel: PaymentMethodViewModel by viewModels()

    private val adapter = PaymentMethodSelectionAdapter(
        clickCallback = {
            viewModel.setEvent(PaymentMethodContract.Event.PaymentInstrumentClicked(it))
        },
        optionsClickCallback = {
            viewModel.setEvent(PaymentMethodContract.Event.PaymentInstrumentOptionsClicked(it))
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_payment_method, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPaymentMethodBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(PaymentMethodContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(PaymentMethodContract.Event.DismissClicked)
            }

            cvAddCard.setOnClickListener {
                viewModel.setEvent(PaymentMethodContract.Event.AddCardClicked)
            }

            val layoutManager = LinearLayoutManager(context)
            rvListCards.adapter = adapter
            rvListCards.layoutManager = layoutManager
            rvListCards.setHasFixedSize(true)
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

        // listen for options bottom sheet dialog result
        parentFragmentManager.setFragmentResultListener(PaymentMethodOptionsBottomSheet.REQUEST_KEY, this) { _, bundle ->
            val result = bundle.getString(FabriikBottomSheet.EXTRA_RESULT_KEY)
            val paymentInstrument = bundle.getParcelable<PaymentInstrument>(
                PaymentMethodOptionsBottomSheet.EXTRA_PAYMENT_INSTRUMENT
            )

            if (result == PaymentMethodOptionsBottomSheet.RESULT_KEY_REMOVE && paymentInstrument != null) {
                viewModel.setEvent(PaymentMethodContract.Event.RemoveOptionClicked(paymentInstrument))
            }
        }

        // listen for removal confirmation dialog result
        requireActivity().supportFragmentManager.setFragmentResultListener(PaymentMethodViewModel.REQUEST_CONFIRMATION_DIALOG, this) { _, bundle ->
            val result = bundle.getString(FabriikGenericDialog.EXTRA_RESULT)
            val paymentInstrument = bundle.getParcelable<PaymentInstrument>(
                PaymentMethodViewModel.EXTRA_CONFIRMATION_DIALOG_DATA
            )

            if (result == PaymentMethodViewModel.RESULT_CONFIRMATION_DIALOG_REMOVE && paymentInstrument != null) {
                viewModel.setEvent(
                    PaymentMethodContract.Event.PaymentInstrumentRemovalConfirmed(paymentInstrument)
                )
            }
        }
    }

    override fun render(state: PaymentMethodContract.State) {
        adapter.submitList(state.paymentInstruments)
        binding.toolbar.setShowDismissButton(state.showDismissButton)
        binding.content.isVisible = !state.initialLoadingIndicator
        binding.loadingIndicator.isVisible = state.initialLoadingIndicator
        binding.fullScreenLoadingView.root.isVisible = state.fullScreenLoadingIndicator
    }

    override fun handleEffect(effect: PaymentMethodContract.Effect) {
        when (effect) {
            is PaymentMethodContract.Effect.Back -> {
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY, bundleOf(RESULT_KEY to effect.result)
                )
                findNavController().popBackStack()
            }

            PaymentMethodContract.Effect.Dismiss ->
                activity?.finish()

            is PaymentMethodContract.Effect.AddCard ->
                findNavController().navigate(
                    PaymentMethodFragmentDirections.actionAddCard(effect.flow)
                )

            is PaymentMethodContract.Effect.ShowError ->
                FabriikToastUtil.showError(binding.root, effect.message)

            is PaymentMethodContract.Effect.ShowToast ->
                FabriikToastUtil.showInfo(binding.root, effect.message)

            is PaymentMethodContract.Effect.ShowConfirmationDialog ->
                FabriikGenericDialog.newInstance(effect.args)
                    .show(parentFragmentManager)

            is PaymentMethodContract.Effect.ShowOptionsBottomSheet ->
                PaymentMethodOptionsBottomSheet.newInstance(effect.paymentInstrument)
                    .show(parentFragmentManager)
        }
    }

    companion object {
        const val REQUEST_KEY = "request_payment_method"
        const val RESULT_KEY = "result_payment_method"
    }

    sealed class Result: Parcelable {
        @Parcelize
        data class Cancelled(val dataUpdated: Boolean): Result()

        @Parcelize
        data class Selected(val paymentInstrument: PaymentInstrument): Result()
    }
}