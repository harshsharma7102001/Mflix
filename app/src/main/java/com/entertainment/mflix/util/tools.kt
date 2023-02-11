package com.entertainment.mflix.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.entertainment.mflix.R

fun launchNativeApi30(context: Context, uri: String): Boolean {
    val nativeAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        .addCategory(Intent.CATEGORY_BROWSABLE)
        .addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER
        )
    return try {
        context.startActivity(nativeAppIntent)
        true
    } catch (ex: ActivityNotFoundException) {
        false
    }
}
fun launchUri(context: Context, uri: String) {
    val launched: Boolean = if (Build.VERSION.SDK_INT >= 30) launchNativeApi30(
        context,
        uri
    ) else {
        custom_tab(uri,context)
        true
    }
    if (!launched) {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(context, Uri.parse(uri))
    }
}


fun custom_tab(url:String,context: Context){
    val builder = CustomTabsIntent.Builder()
    val params = CustomTabColorSchemeParams.Builder()
    params.setToolbarColor(ContextCompat.getColor(context, R.color.purple_700))
    builder.setDefaultColorSchemeParams(params.build())

    builder.setShowTitle(true)
    builder.setInstantAppsEnabled(true)
    val customBuilder = builder.build()
    if (context.isPackageInstalled("com.android.chrome")) {
        // if chrome is available use chrome custom tabs
        customBuilder.intent.setPackage("com.android.chrome")
        customBuilder.launchUrl(context, Uri.parse(url))
    } else {
//        createToast("Kindly Check Your Internet Connection")
    }
}
//fun createToast(s:String){
//    Toast.makeText(context,"$s", Toast.LENGTH_SHORT).show()
//}
fun Context.isPackageInstalled(packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}
