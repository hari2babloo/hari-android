package io.scal.ambi.ui.webview

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ambi.work.R
import com.ambi.work.databinding.FragmentCalendarListWebViewBinding
import io.scal.ambi.ui.global.base.fragment.BaseNavigationFragment
import kotlin.reflect.KClass


open class WebViewFragment : BaseNavigationFragment<WebViewViewModel, FragmentCalendarListWebViewBinding>() {

    override val layoutId: Int = R.layout.fragment_calendar_list_web_view
    override val viewModelClass: KClass<WebViewViewModel> = WebViewViewModel::class

    private val safeBaseServerUrl = "https://jinn.tech/"
    private val notSafeBaseServerUrl = "http://jinn.tech/"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scheduleUrl = getStartUrl()
        binding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            setSupportZoom(true)
        }
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return if (url.startsWith(safeBaseServerUrl) || url.startsWith(notSafeBaseServerUrl)) {
                    view.loadUrl(url)
                    false
                } else {    
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        // pass
                    }
                    true
                }
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                binding.progress.visibility = View.VISIBLE
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                binding.progress.visibility = View.GONE
                super.onPageFinished(view, url)
            }
        }
        binding.webView.loadUrl(scheduleUrl)
    }

    open fun getStartUrl(): String {
        return arguments?.getString("url") ?: safeBaseServerUrl
    }

    override fun onBackPressed(): Boolean =
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
            true
        } else {
            super.onBackPressed()
        }

    companion object {

        fun createScreen(url: String): WebViewFragment {
            val fragment = WebViewFragment()
            val args = Bundle()
            args.putString("url", url)
            fragment.arguments = args
            return fragment
        }
    }
}