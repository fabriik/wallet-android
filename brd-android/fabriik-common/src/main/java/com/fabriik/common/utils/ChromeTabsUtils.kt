package com.fabriik.common.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

object ChromeTabsUtils {

    private var chromePackageName = "com.android.chrome";

    fun showUrl(context: Context, url: String) {
        val uri = Uri.parse(url)
        val tabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(false)
            .build()

        if (isChromeInstalled(context)) {
            // if chrome is available use chrome custom tabs
            tabsIntent.intent.setPackage(chromePackageName)
            tabsIntent.launchUrl(context, uri)
        } else {
            // if not available use browser to launch the url
            context.startActivity(
                Intent(Intent.ACTION_VIEW, uri)
            )
        }
    }

    private fun isChromeInstalled(context: Context): Boolean {
        // check if chrome is installed or not
        return try {
            context.packageManager.getPackageInfo(chromePackageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}