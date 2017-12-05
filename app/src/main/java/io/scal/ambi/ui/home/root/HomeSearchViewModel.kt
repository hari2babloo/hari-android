package io.scal.ambi.ui.home.root

import android.content.Context
import io.scal.ambi.ui.global.search.SearchViewModel
import io.scal.ambi.ui.global.base.LocalCiceroneHolder
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Inject

class HomeSearchViewModel @Inject constructor(context: Context) : SearchViewModel(context) {

    private val localCiceroneHolder = LocalCiceroneHolder()

    fun getNavigationHolder(tag: String): NavigatorHolder =
        localCiceroneHolder.getCicerone(tag).navigatorHolder
}