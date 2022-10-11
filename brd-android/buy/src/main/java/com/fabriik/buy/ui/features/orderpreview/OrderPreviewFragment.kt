package com.fabriik.buy.ui.features.orderpreview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.breadwallet.breadbox.formatCryptoForUi
import com.breadwallet.util.formatFiatForUi
import com.fabriik.buy.R
import com.fabriik.buy.databinding.FragmentOrderPreviewBinding
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.common.ui.dialog.InfoDialog
import com.fabriik.common.ui.dialog.InfoDialogArgs
import com.fabriik.common.utils.FabriikToastUtil
import com.fabriik.common.utils.formatPercent
import com.fabriik.common.utils.textOrEmpty
import com.fabriik.trade.ui.features.authentication.SwapAuthenticationViewModel
import kotlinx.coroutines.flow.collect

class OrderPreviewFragment : Fragment(),
    FabriikView<OrderPreviewContract.State, OrderPreviewContract.Effect> {

    private lateinit var binding: FragmentOrderPreviewBinding
    private val viewModel: OrderPreviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
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

            etCvv.addTextChangedListener {
                viewModel.setEvent(
                    OrderPreviewContract.Event.OnSecurityCodeChanged(
                        it.textOrEmpty()
                    )
                )
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnConfirmClicked)
            }

            val clickableText = getString(R.string.About_terms)
            val fullText = getString(R.string.Buy_Terms_android, clickableText)
            tvTermsConditions.text = getSpannableText(fullText, clickableText)
            tvTermsConditions.movementMethod = LinkMovementMethod.getInstance()
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
        with(binding) {
            ivCrypto.postLoadIcon(state.cryptoCurrency)
            btnConfirm.isEnabled = state.confirmButtonEnabled
            viewCreditCard.setPaymentInstrument(state.paymentInstrument)

            tvTotalAmount.text = state.totalFiatAmount.formatFiatForUi(
                currencyCode = state.fiatCurrency,
                showCurrencyName = true
            )

            tvAmountValue.text = state.fiatAmount.formatFiatForUi(
                currencyCode = state.fiatCurrency,
                showCurrencyName = true
            )

            tvCryptoAmount.text = state.cryptoAmount.formatCryptoForUi(state.cryptoCurrency, 8)

            tvCreditFeeValue.text = state.cardFee.formatFiatForUi(
                currencyCode = state.fiatCurrency,
                showCurrencyName = true
            )

            tvCreditFeeTitle.text = "${getString(R.string.Swap_CardFee)} (${state.cardFeePercent.formatPercent()})"

            tvNetworkFeeValue.text = state.networkFee.formatFiatForUi()

            tvRateValue.text = RATE_FORMAT.format(
                state.cryptoCurrency,
                state.oneCryptoUnitToFiatRate.formatFiatForUi(
                    currencyCode = state.fiatCurrency,
                    showCurrencyName = true
                )
            )

            fullScreenLoadingView.root.isVisible = state.fullScreenLoadingIndicator
        }
    }

    override fun handleEffect(effect: OrderPreviewContract.Effect) {
        when (effect) {
            OrderPreviewContract.Effect.Back ->
                findNavController().popBackStack()

            OrderPreviewContract.Effect.Dismiss ->
                activity?.finish()

            OrderPreviewContract.Effect.TimeoutScreen ->
                findNavController().navigate(
                    OrderPreviewFragmentDirections.actionPaymentTimeout()
                )

            is OrderPreviewContract.Effect.PaymentProcessing -> {
                // set callback
                parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY to RESULT_NEW_QUOTE))

                findNavController().navigate(
                    OrderPreviewFragmentDirections.actionPaymentProcessing(
                        redirectUrl = effect.redirectUrl,
                        paymentReference = effect.paymentReference
                    )
                )
            }

            OrderPreviewContract.Effect.RequestUserAuthentication ->
                findNavController().navigate(
                    OrderPreviewFragmentDirections.actionUserAuthentication()
                )

            is OrderPreviewContract.Effect.ShowError ->
                FabriikToastUtil.showError(binding.root, effect.message)

            is OrderPreviewContract.Effect.ShowInfoDialog ->
                showInfoDialog(effect)

            is OrderPreviewContract.Effect.OpenWebsite -> {
                val uri = Uri.parse(effect.url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
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
    
    companion object {
        private const val RATE_FORMAT = "1 %s = %s"
        const val REQUEST_KEY = "request_order_preview"
        const val RESULT_KEY = "result_order_preview"
        const val RESULT_NEW_QUOTE = "quote_used"
    }
}