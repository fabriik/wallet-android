package com.fabriik.kyc.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
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
            btnProfileInfo.setOnClickListener {
                callback?.onButtonClicked(StatusButton.VERIFY_ACCOUNT)
            }

            btnUpgradeLimits.setOnClickListener {
                callback?.onButtonClicked(StatusButton.UPGRADE_LIMITS)
            }

            btnDeclinedVerificationMoreInfo.setOnClickListener {
                callback?.onButtonClicked(StatusButton.VERIFICATION_DECLINED_INFO)
            }
        }
    }

    fun setStatus(status: AccountVerificationStatus) {
        val state = when (status) {
            AccountVerificationStatus.NONE -> State.Default
            AccountVerificationStatus.LEVEL1_VERIFIED -> State.Level1Verified
            AccountVerificationStatus.LEVEL2_PENDING -> State.Level2Pending
            AccountVerificationStatus.LEVEL2_DECLINED -> State.Level2Declined
            AccountVerificationStatus.LEVEL2_RESUBMIT -> State.Level2Resubmit
            AccountVerificationStatus.LEVEL2_VERIFIED -> State.Level2Verified
        }

        with(binding) {
            tvTitle.setText(state.title)
            tvSubtitle.setText(state.subtitle)

            tvStatus.isVisible = state.statusView != null
            btnVerifyAccount.isVisible = state.verifyAccountButtonVisible
            btnUpgradeLimits.isVisible = state.upgradeLimitsButtonVisible
            btnDeclinedVerificationMoreInfo.isVisible = state.verificationDeclinedButtonVisible

            // setup Status TextView
            state.statusView?.let {
                tvStatus.backgroundTintList = ContextCompat.getColorStateList(
                    context, state.statusView.backgroundTint
                )

                tvStatus.setTextColor(
                    ContextCompat.getColor(
                        context, state.statusView.textColor
                    )
                )
            }
        }
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onButtonClicked(button: StatusButton)
    }

    private sealed class StatusViewState(
        @ColorRes val textColor: Int,
        @ColorRes val backgroundTint: Int
    ) {

        object Pending : StatusViewState(
            textColor = R.color.light_contrast_02,
            backgroundTint = R.color.light_pending
        )

        object Verified : StatusViewState(
            textColor = R.color.light_contrast_01,
            backgroundTint = R.color.light_success
        )

        object Resubmit : StatusViewState(
            textColor = R.color.light_contrast_01,
            backgroundTint = R.color.light_error
        )

        object Declined : StatusViewState(
            textColor = R.color.light_contrast_01,
            backgroundTint = R.color.light_error
        )
    }

    private sealed class State(
        @StringRes val title: Int,
        @StringRes val subtitle: Int,
        val statusView: StatusViewState? = null,
        val upgradeLimitsButtonVisible: Boolean = false,
        val verifyAccountButtonVisible: Boolean = false,
        val verificationDeclinedButtonVisible: Boolean = false
    ) {
        object Default : State(
            title = R.string.ProfileStatusView_accountLimits,
            subtitle = R.string.ProfileStatusView_statusSubtitleDefault,
            verifyAccountButtonVisible = true
        )

        object Level1Verified : State(
            title = R.string.ProfileStatusView_accountLimits,
            subtitle = R.string.ProfileStatusView_statusSubtitleLevel1Verified,
            statusView = StatusViewState.Verified,
            upgradeLimitsButtonVisible = true
        )

        object Level2Pending : State(
            title = R.string.ProfileStatusView_accountLimits,
            subtitle = R.string.ProfileStatusView_statusSubtitleLevel2Pending,
            statusView = StatusViewState.Pending
        )

        object Level2Resubmit : State(
            title = R.string.ProfileStatusView_accountLimits,
            subtitle = R.string.ProfileStatusView_statusSubtitleLevel2Failure,
            statusView = StatusViewState.Resubmit,
            verificationDeclinedButtonVisible = true
        )

        object Level2Declined : State(
            title = R.string.ProfileStatusView_accountLimits,
            subtitle = R.string.ProfileStatusView_statusSubtitleLevel2Failure,
            statusView = StatusViewState.Declined,
            verificationDeclinedButtonVisible = true
        )

        object Level2Verified : State(
            title = R.string.ProfileStatusView_accountLimits,
            subtitle = R.string.ProfileStatusView_statusSubtitleLevel2Verified,
            statusView = StatusViewState.Verified
        )
    }

    enum class StatusButton {
        VERIFY_ACCOUNT,
        UPGRADE_LIMITS,
        VERIFICATION_DECLINED_INFO
    }
}