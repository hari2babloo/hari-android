package io.scal.ambi.ui.global.search

import android.content.Context
import android.databinding.ObservableBoolean
import io.scal.ambi.R
import io.scal.ambi.extensions.binding.ObservableString
import io.scal.ambi.ui.global.BaseViewModel

abstract class SearchViewModel(val hint: String) : BaseViewModel() {

    constructor(context: Context) : this(context.getString(R.string.hint_search))

    val expandView = ObservableBoolean(false)
    val searchText = ObservableString()
}