package io.scal.ambi.ui.global.base

import android.arch.lifecycle.ViewModel
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import java.util.*

class LocalCiceroneHolderViewModel : ViewModel() {

    private val containers: HashMap<String, Cicerone<BetterRouter>> = HashMap()

    fun getNavigationHolder(tag: String, router: BetterRouter?): NavigatorHolder {
        return getCicerone(tag, router).navigatorHolder
    }

    fun getRouter(tag: String, router: BetterRouter?): BetterRouter {
        return getCicerone(tag, router).router
    }

    private fun getCicerone(containerTag: String, router: BetterRouter?): Cicerone<BetterRouter> {
        if (!containers.containsKey(containerTag)) {
            containers.put(containerTag, Cicerone.create(BetterRouter(router)))
        }
        return containers[containerTag]!!
    }
}