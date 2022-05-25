package com.fabriik.kyc.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.fabriik.kyc.R
import com.fabriik.kyc.data.enums.AccountVerificationStatus
import com.fabriik.kyc.databinding.PartialVerificationStatusBinding

class AccountVerificationStatusView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private var callback: Callback? = null
    private val binding: PartialVerificationStatusBinding

    init {
        setBackgroundResource(R.drawable.bg_info_prompt)
        binding = PartialVerificationStatusBinding.inflate(
            LayoutInflater.from(context), this
        )

        with(binding) {
            btnProfileInfo.setOnClickListener { callback?.onVerifyProfileInfoClicked() }
            btnVerifyAccount.setOnClickListener { callback?.onVerifyProfileClicked() }
        }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setStatus(status: AccountVerificationStatus) {
        //todo: tvStatus.isVisible = status != AccountVerificationStatus.None
        //todo: tvStatus.textColor = if (status == AccountVerificationStatus.None) R.color.light_contrast_01 else R.color.light_contrast_01

        //todo
        val state = when (status) {
            is AccountVerificationStatus.None -> UiState.None
            is AccountVerificationStatus.Basic -> if (status.verified) UiState.BasicVerified else UiState.BasicPending
            is AccountVerificationStatus.Unlimited -> if (status.verified) UiState.UnlimitedVerified else UiState.UnlimitedPending
        }

        setBackgroundResource(
            state.viewBackground
        )

        when(state.statusViewState) {
            is StatusViewState.Invisible -> binding.tvStatus.isVisible = false
            is StatusViewState.Visible -> {
                binding.tvStatus.isVisible = true
                binding.tvStatus.setBackgroundResource(state.statusViewState.background)
                binding.tvStatus.setTextColor(ContextCompat.getColor(context, state.statusViewState.textColor))
            }
        }

        // todo: set state to binding
    }

    interface Callback {
        fun onVerifyProfileClicked()
        fun onVerifyProfileInfoClicked()
    }

    private sealed class UiState(
        @DrawableRes val viewBackground: Int,
        val statusViewState: StatusViewState
    ) {
        object None : UiState(
            viewBackground = R.drawable.bg_info_prompt,
            statusViewState = StatusViewState.Invisible
        )

        object BasicPending : UiState(
            viewBackground = R.drawable.bg_info_prompt_white_with_border,
            statusViewState = StatusViewState.Visible.Pending
        )

        object BasicVerified : UiState(
            viewBackground = R.drawable.bg_info_prompt_white_with_border,
            statusViewState = StatusViewState.Visible.Verified
        )

        object UnlimitedPending : UiState(
            viewBackground = R.drawable.bg_info_prompt_white_with_border,
            statusViewState = StatusViewState.Visible.Pending
        )

        object UnlimitedVerified : UiState(
            viewBackground = R.drawable.bg_info_prompt_white_with_border,
            statusViewState = StatusViewState.Visible.Verified
        )
    }

    private sealed class StatusViewState {
        object Invisible : StatusViewState()
        sealed class Visible(
            @ColorRes val textColor: Int,
            @DrawableRes val background: Int,
        ) : StatusViewState() {

            object Pending : Visible(
                textColor = R.color.light_contrast_02,
                background = R.drawable.bg_status_pending
            )

            object Verified : Visible(
                textColor = R.color.light_contrast_01,
                background = R.drawable.bg_status_verified
            )
        }
    }
}