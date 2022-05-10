package com.fabriik.kyc.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper

class TakePictureWithRequestCodeContract :
    ActivityResultContract<Pair<String, Uri>, Pair<String, Uri>?>() {

    private lateinit var imageUri: Uri
    private lateinit var requestKey: String

    @CallSuper
    override fun createIntent(context: Context, input: Pair<String, Uri>): Intent {
        imageUri = input.second
        requestKey = input.first

        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .putExtra(MediaStore.EXTRA_OUTPUT, input.second)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<String, Uri>? {
        return when (resultCode) {
            Activity.RESULT_OK -> requestKey to imageUri
            else -> null
        }
    }
}