package io.scal.ambi.ui.profile.details

import android.content.Context
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.search.SearchViewModel
import javax.inject.Inject

internal class ProfileSearchViewModel @Inject constructor(context: Context,
                                                          router: BetterRouter) : SearchViewModel(context, router) {
}