package com.fabriik.buy.ui.orderPreview

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
import com.fabriik.buy.databinding.FragmentOrderPreviewBinding
import com.fabriik.common.ui.base.FabriikView
import kotlinx.coroutines.flow.collect

class OrderPreviewFragment : Fragment(),
    FabriikView<OrderPreviewContract.State, OrderPreviewContract.Effect> {

    private lateinit var binding: FragmentOrderPreviewBinding
    private val viewModel: OrderPreviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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

            icInfoCredit.setOnClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnCreditInfoClicked)
            }

            icInfoNetwork.setOnClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnNetworkInfoClicked)
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(OrderPreviewContract.Event.OnConfirmClicked)
            }

            val clickableText = getString(R.string.Buy_OrderPreview_TermsCondition)
            val fullText = getString(R.string.Buy_OrderPreview_Subtext, clickableText)
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
    }

    override fun render(state: OrderPreviewContract.State) {
    }

    override fun handleEffect(effect: OrderPreviewContract.Effect) {
        when (effect) {
            OrderPreviewContract.Effect.Back ->
                findNavController().popBackStack()

            OrderPreviewContract.Effect.Dismiss ->
                activity?.finish()

            is OrderPreviewContract.Effect.InfoDialog -> TODO()
        }
    }

    private fun getSpannableText(fullText: String, clickableText: String): SpannableString {
        val spannableString = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                viewModel.setEvent(OrderPreviewContract.Event.OnTermsAndConditionsCLicked)
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
}