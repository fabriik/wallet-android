package com.fabriik.buy.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.ViewModelProvider
import com.fabriik.buy.R
import com.fabriik.buy.data.WyreApi
import com.fabriik.common.data.Status

class BuyWebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var viewModel: BuyWebViewViewModel
    private lateinit var loadingIndicator: ContentLoadingProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_web_view)

        viewModel = ViewModelProvider(this)
            .get(BuyWebViewViewModel::class.java)

        webView = findViewById(R.id.web_view)
        loadingIndicator = findViewById(R.id.loading_bar)
        loadingIndicator.isVisible = true

        webView.settings.apply {
            domStorageEnabled = true
            javaScriptEnabled = true
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val trimmedUrl = request.url.toString().trimEnd('/')

                when {
                    trimmedUrl.startsWith("file://") ->
                        view.loadUrl(trimmedUrl)
                    trimmedUrl == "${WyreApi.REDIRECT_URL}?" ->
                        finishWithResult(RESULT_CANCELED)
                    trimmedUrl.startsWith(WyreApi.REDIRECT_URL) ->
                        finishWithResult(RESULT_SUCCESS)
                    trimmedUrl.startsWith(WyreApi.FAILURE_REDIRECT_URL) ->
                        Log.d("wyre", "Wyre flow failed: $trimmedUrl")
                    else ->
                        return false // Wyre links
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                loadingIndicator.isVisible = false
            }
        }

        viewModel.getPaymentUrl().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { response ->
                        webView.loadUrl(
                            response.url
                        )
                    }
                }
                Status.ERROR -> {
                    finishWithResult(RESULT_ERROR)
                }
            }
        }
    }

    private fun finishWithResult(resultCode: Int) {
        setResult(resultCode)
        finish()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val RESULT_ERROR = 2131
        const val RESULT_SUCCESS = 2132
        const val RESULT_CANCELED = 2133

        fun getStartIntent(context: Context) = Intent(context, BuyWebViewActivity::class.java)
    }
}