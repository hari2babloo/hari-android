package io.scal.ambi.ui.global.base

import ru.terrakok.cicerone.NavigatorHolder

interface LocalNavigationHolder {

    fun getNavigationHolder(tag: String): NavigatorHolder

    fun getRouter(tag: String): BetterRouter
}