package com.fabriik.kyc.ui.features.postvalidation

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class PostValidationViewModel(
    application: Application
) : FabriikViewModel<PostValidationContract.State, PostValidationContract.Event, PostValidationContract.Effect>(
    application
) {
    override fun createInitialState() = PostValidationContract.State

    override fun handleEvent(event: PostValidationContract.Event) {
        when (event) {
            is PostValidationContract.Event.BackClicked ->
                setEffect { PostValidationContract.Effect.Back }

            is PostValidationContract.Event.ConfirmClicked ->
                setEffect { PostValidationContract.Effect.Profile }

            is PostValidationContract.Event.DismissClicked ->
                setEffect { PostValidationContract.Effect.Dismiss }
        }
    }
}