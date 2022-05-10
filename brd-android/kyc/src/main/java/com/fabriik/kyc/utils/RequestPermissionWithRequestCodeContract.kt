package com.fabriik.kyc.utils

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper

class RequestPermissionWithRequestCodeContract : ActivityResultContract<Pair<String, String>, Pair<String, Boolean>>() {

    private lateinit var requestKey: String

    private val requestPermissionContract = ActivityResultContracts.RequestPermission()

    @CallSuper
    override fun createIntent(context: Context, input: Pair<String, String>) : Intent {
        requestKey = input.first
        return requestPermissionContract.createIntent(context, input.second)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<String, Boolean> {
        val result = requestPermissionContract.parseResult(resultCode, intent)
        return requestKey to result
    }

    override fun getSynchronousResult(context: Context, input: Pair<String, String>): SynchronousResult<Pair<String, Boolean>>? {
        val result = requestPermissionContract.getSynchronousResult(context, input.second) ?: return null
        return SynchronousResult(input.first to result.value)
    }
}