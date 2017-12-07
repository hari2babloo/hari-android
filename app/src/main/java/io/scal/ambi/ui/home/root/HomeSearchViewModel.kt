package io.scal.ambi.ui.home.root

import android.content.Context
import io.scal.ambi.ui.global.search.SearchViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

internal class HomeSearchViewModel @Inject constructor(context: Context, router: Router) : SearchViewModel(context, router) {

}