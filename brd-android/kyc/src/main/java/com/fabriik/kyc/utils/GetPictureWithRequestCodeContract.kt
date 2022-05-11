package com.fabriik.kyc.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper

class GetPictureWithRequestCodeContract :
    ActivityResultContract<String, Pair<String, Uri>?>() {

    private lateinit var requestKey: String

    @CallSuper
    override fun createIntent(context: Context, input: String): Intent {
        requestKey = input

        return Intent(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("image/*")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<String, Uri>? {
        val imageUri = intent?.data

        return when {
            resultCode == Activity.RESULT_OK && imageUri != null -> requestKey to imageUri
            else -> null
        }
    }
}