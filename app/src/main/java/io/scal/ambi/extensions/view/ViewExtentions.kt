package io.scal.ambi.extensions.view

import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.LocalNavigationHolder
import ru.terrakok.cicerone.NavigatorHolder

fun View.enableCascade(enable: Boolean) {
    if (this is ViewGroup) {
        (0 until childCount)
            .map { getChildAt(it) }
            .forEach { it.enableCascade(enable) }
    }
    this.isEnabled = enable
}

fun Fragment.getNavigationHolder(): NavigatorHolder {
    return getLocalNavigationHolder().getNavigationHolder(tag.notNullOrThrow("tag"))
}

fun Fragment.getRouter(): BetterRouter {
    return getLocalNavigationHolder().getRouter(tag.notNullOrThrow("tag"))
}

private fun Fragment.getLocalNavigationHolder(): LocalNavigationHolder {
    var localNavigationHolder: LocalNavigationHolder? = null

    var parentFragment: Fragment? = parentFragment
    while (null != parentFragment) {
        if (parentFragment is LocalNavigationHolder) {
            localNavigationHolder = parentFragment
            break
        }
        parentFragment = parentFragment.parentFragment
    }

    if (null == localNavigationHolder && activity is LocalNavigationHolder) {
        localNavigationHolder = activity as LocalNavigationHolder
    }

    if (null == localNavigationHolder && activity?.application is LocalNavigationHolder) {
        localNavigationHolder = activity!!.application as LocalNavigationHolder
    }

    if (null == localNavigationHolder) {
        throw IllegalArgumentException("No injector was found for ${javaClass.canonicalName}")
    }
    return localNavigationHolder
}