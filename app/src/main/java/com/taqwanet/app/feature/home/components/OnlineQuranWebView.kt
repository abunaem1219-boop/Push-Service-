package com.taqwanet.app.feature.quran.components

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.taqwanet.app.core.theme.MintBright

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OnlineQuranWebView(
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(true) }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                        }
                    }
                    webChromeClient = WebChromeClient()

                    // লোকাল assets অথবা অনলাইন বিশ্বস্ত কুরআন পোর্টাল
                    try {
                        loadUrl("file:///android_asset/online_quran/index.html")
                    } catch (e: Exception) {
                        loadUrl("https://quran.com")
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            CircularProgressIndicator(
                color = MintBright,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}