package io.scal.ambi.ui.global.base

import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

interface LocalNavigationHolder {

    fun getNavigationHolder(tag: String): NavigatorHolder

    fun getRouter(tag: String): Router
}