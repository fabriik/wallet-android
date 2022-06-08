package com.breadwallet.ui.profile

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.breadwallet.databinding.ControllerProfileBinding
import com.breadwallet.ui.BaseMobiusController
import com.breadwallet.ui.flowbind.clicks
import com.breadwallet.ui.profile.ProfileScreen.E
import com.breadwallet.ui.profile.ProfileScreen.F
import com.breadwallet.ui.profile.ProfileScreen.M
import com.fabriik.kyc.ui.customview.AccountVerificationStatusView
import com.spotify.mobius.Connectable
import com.spotify.mobius.First
import com.spotify.mobius.Init
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class
ProfileController(
    args: Bundle? = null
) : BaseMobiusController<M, E, F>(args) {

    override val init = Init<M, F> { model ->
        First.first(
            model, setOf(
                F.LoadOptions,
                F.LoadProfileData
            )
        )
    }

    override val defaultModel = M.createDefault()
    override val update = ProfileUpdate
    override val effectHandler = Connectable<F, E> { output ->
        ProfileScreenHandler(
            output,
            applicationContext!!
        )
    }

    private val binding by viewBinding(ControllerProfileBinding::inflate)

    override fun onCreateView(view: View) {
        super.onCreateView(view)
        binding.settingsList.layoutManager = LinearLayoutManager(activity!!)
        binding.settingsList.addItemDecoration(
            DividerItemDecoration(
                activity!!, DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun bindView(modelFlow: Flow<M>): Flow<E> {
        return with(binding) {
            viewProfileStatus.setCallback(object : AccountVerificationStatusView.Callback {
                override fun onButtonClicked(button: AccountVerificationStatusView.StatusButton) {
                    eventConsumer.accept(
                        when (button) {
                            AccountVerificationStatusView.StatusButton.VERIFY_ACCOUNT ->
                                E.OnVerifyProfileClicked

                            AccountVerificationStatusView.StatusButton.UPGRADE_LIMITS ->
                                E.OnUpgradeLimitsClicked

                            AccountVerificationStatusView.StatusButton.VERIFICATION_MORE_INFO ->
                                E.OnVerificationMoreInfoClicked

                            AccountVerificationStatusView.StatusButton.VERIFICATION_DECLINED_INFO ->
                                E.OnVerificationDeclinedInfoClicked
                        }
                    )
                }
            })

            merge(
                btnClose.clicks().map { E.OnCloseClicked },
                btnChangeEmail.clicks().map { E.OnChangeEmailClicked }
            )
        }
    }

    override fun M.render() {
        with(binding) {
            ifChanged(M::items) {
                val adapter = ProfileAdapter(items) { option ->
                    eventConsumer.accept(E.OnOptionClicked(option))
                }
                settingsList.adapter = adapter
            }
            ifChanged(M::isLoading) {
                loadingView.root.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
            ifChanged(M::profile) { data ->
                data?.let {
                    tvProfileName.text = it.email
                    viewProfileStatus.setStatus(it.kycStatus)
                }
            }
        }
    }
}
