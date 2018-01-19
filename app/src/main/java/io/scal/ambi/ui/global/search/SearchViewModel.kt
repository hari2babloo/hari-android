package io.scal.ambi.ui.global.search

import android.content.Context
import android.databinding.ObservableBoolean
import com.ambi.work.R
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel

abstract class SearchViewModel(val hint: String, router: BetterRouter) : BaseViewModel(router) {

    constructor(context: Context, router: BetterRouter) : this(context.getString(R.string.hint_search), router)

    val expandView = ObservableBoolean(false)
    val searchText = ObservableString()

    override fun onBackPressed(): Boolean = false
}