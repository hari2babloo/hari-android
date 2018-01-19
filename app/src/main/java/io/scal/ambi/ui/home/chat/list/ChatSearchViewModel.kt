package io.scal.ambi.ui.home.chat.list

import android.content.Context
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.search.SearchViewModel
import javax.inject.Inject

internal class ChatSearchViewModel @Inject constructor(context: Context,
                                                       router: BetterRouter) : SearchViewModel(context, router) {
}