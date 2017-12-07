package io.scal.ambi.ui.global.search

import android.content.Context
import android.databinding.ObservableBoolean
import io.scal.ambi.R
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import ru.terrakok.cicerone.Router

abstract class SearchViewModel(val hint: String, router: Router) : BaseViewModel(router) {

    constructor(context: Context, router: Router) : this(context.getString(R.string.hint_search), router)

    val expandView = ObservableBoolean(false)
    val searchText = ObservableString()

    override fun onBackPressed(): Boolean = false
}