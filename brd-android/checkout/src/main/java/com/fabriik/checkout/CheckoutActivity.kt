package com.fabriik.checkout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.checkout.android_sdk.PaymentForm
import com.checkout.android_sdk.Response.CardTokenisationFail
import com.checkout.android_sdk.Response.CardTokenisationResponse
import com.checkout.android_sdk.Utils.Environment
import com.checkout.android_sdk.network.NetworkError

class CheckoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        // initialise the payment from
        val mPaymentForm = findViewById<PaymentForm>(R.id.checkout_card_form)
        val mFormListener: PaymentForm.PaymentFormCallback = object : PaymentForm.PaymentFormCallback {
            override fun onFormSubmit() {
                Log.d("Tag", "onFormSubmit")
                // form submit initiated; you can potentially display a loader
            }

            override fun onTokenGenerated(response: CardTokenisationResponse?) {
                Log.d("Tag", "onTokenGenerated")
                // your token is here
                //mPaymentForm.clearForm(); // this clears the Payment Form
            }

            override fun onError(response: CardTokenisationFail?) {
                Log.d("Tag", "onError")
                // token request error
            }

            override fun onNetworkError(error: NetworkError?) {
                Log.d("Tag", "onNetworkError")
                // network error
            }

            override fun onBackPressed() {
                Log.d("Tag", "onBackPressed")
                // the user decided to leave the payment page
                mPaymentForm.clearForm() // this clears the Payment Form
            }
        }
        mPaymentForm.setFormListener(mFormListener) // set the callback
            .setEnvironment(Environment.SANDBOX) // set the environemnt
            .setKey("pk_sbox_ees63clhrko6kta6j3cwloebg4#")
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, CheckoutActivity::class.java)
        }
    }
}