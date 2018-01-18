package io.scal.ambi.ui.home.calendar.list

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


class CalendarListWebViewFragment : BaseNavigationFragment<CalendarListWebViewViewModel, FragmentCalendarListWebViewBinding>() {

    override val layoutId: Int = R.layout.fragment_calendar_list_web_view
    override val viewModelClass: KClass<CalendarListWebViewViewModel> = CalendarListWebViewViewModel::class

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return if (true || url.contains("ambi.work")) {
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
        binding.webView.loadUrl("https://google.com")
    }

    override fun onBackPressed(): Boolean =
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
            true
        } else {
            super.onBackPressed()
        }
}