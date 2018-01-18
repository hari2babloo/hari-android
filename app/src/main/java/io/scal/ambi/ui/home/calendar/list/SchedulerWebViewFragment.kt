package io.scal.ambi.ui.home.calendar.list

import io.scal.ambi.ui.webview.WebViewFragment

class SchedulerWebViewFragment : WebViewFragment() {

    override fun getStartUrl(): String {
        return "https://jinn.tech/ambi/schedule"
    }
}