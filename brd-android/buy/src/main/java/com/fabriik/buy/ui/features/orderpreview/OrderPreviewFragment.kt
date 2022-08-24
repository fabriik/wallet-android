package com.fabriik.buy.ui.features.orderpreview

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.buy.R
import com.fabriik.buy.data.enums.CardType
import com.fabriik.buy.databinding.FragmentOrderPreviewBinding
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.common.ui.dialog.InfoDialog
import com.fabriik.common.ui.dialog.InfoDialogArgs
import com.fabriik.trade.ui.features.authentication.SwapAuthenticationViewModel
import kotlinx.coroutines.flow.collect

class OrderPreviewFragment : Fragment(),
    FabriikView<OrderPreviewContract.State, OrderPreviewContract.Effect> {

    private lateinit var binding: FragmentOrderPreviewBinding
    private val viewModel: OrderPreviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOrderPreviewBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnBackPressed)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnDismissClicked)
            }

            ivInfoCredit.setOnClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnCreditInfoClicked)
            }

            ivInfoNetwork.setOnClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnNetworkInfoClicked)
            }

            ivInfoSecurityCode.setOnClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnSecurityCodeInfoClicked)
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnConfirmClicked)
            }

            val clickableText = getString(R.string.Buy_OrderPreview_TermsCondition)
            val fullText = getString(R.string.Buy_OrderPreview_Subtext, clickableText)
            tvTermsConditions.text = getSpannableText(fullText, clickableText)
            tvTermsConditions.movementMethod = LinkMovementMethod.getInstance()

            //TODO - connect to BE
            viewCreditCard.setData(
                type = CardType.VISA,
                lastDigits = "4255",
                expirationDate = "25/59"
            )
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

        // listen for user authentication result
        parentFragmentManager.setFragmentResultListener(SwapAuthenticationViewModel.REQUEST_KEY, this) { _, bundle ->
            val resultKey = bundle.getString(SwapAuthenticationViewModel.EXTRA_RESULT)
            if (resultKey == SwapAuthenticationViewModel.RESULT_KEY_SUCCESS) {
                binding.root.post {
                    viewModel.setEvent(OrderPreviewContract.Event.OnUserAuthenticationSucceed)
                }
            }
        }
    }

    override fun render(state: OrderPreviewContract.State) {
    }

    override fun handleEffect(effect: OrderPreviewContract.Effect) {
        when (effect) {
            OrderPreviewContract.Effect.Back ->
                findNavController().popBackStack()

            OrderPreviewContract.Effect.Dismiss ->
                activity?.finish()

            OrderPreviewContract.Effect.PaymentProcessing ->
                findNavController().navigate(
                    OrderPreviewFragmentDirections.actionPaymentProcessing()
                )

            OrderPreviewContract.Effect.RequestUserAuthentication ->
                findNavController().navigate(
                    OrderPreviewFragmentDirections.actionUserAuthentication()
                )

            is OrderPreviewContract.Effect.ShowInfoDialog ->
                showInfoDialog(effect)
        }
    }

    private fun getSpannableText(fullText: String, clickableText: String): SpannableString {
        val spannableString = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                viewModel.setEvent(OrderPreviewContract.Event.OnTermsAndConditionsClicked)
            }
        }

        val index = fullText.indexOf(clickableText)
        spannableString.setSpan(
            clickableSpan,
            index,
            index + clickableText.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

    private fun showInfoDialog(effect: OrderPreviewContract.Effect.ShowInfoDialog) {
        val args = InfoDialogArgs(
            image = effect.image,
            title = effect.title,
            description = effect.description
        )

        InfoDialog(args).show(parentFragmentManager, InfoDialog.TAG)
    }
}