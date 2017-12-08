package io.scal.ambi.ui.global.base

import android.arch.lifecycle.ViewModel
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.result.ResultListener
import java.util.*

class LocalCiceroneHolderViewModel : ViewModel() {

    private val containers: HashMap<String, Cicerone<Router>> = HashMap()

    fun getNavigationHolder(tag: String, router: Router?): NavigatorHolder {
        return getCicerone(tag, router).navigatorHolder
    }

    fun getRouter(tag: String, router: Router?): Router {
        return getCicerone(tag, router).router
    }

    private fun getCicerone(containerTag: String, router: Router?): Cicerone<Router> {
        if (!containers.containsKey(containerTag)) {
            containers.put(containerTag, Cicerone.create(CustomInnerRouter(router)))
        }
        return containers[containerTag]!!
    }

    private class CustomInnerRouter(private val parentRouter: Router?) : Router() {

        override fun setResultListener(resultCode: Int, listener: ResultListener?) {
            super.setResultListener(resultCode, listener)
            parentRouter?.setResultListener(resultCode, {
                sendResult(resultCode, it)
            })
        }

        override fun removeResultListener(resultCode: Int) {
            parentRouter?.removeResultListener(resultCode)
            super.removeResultListener(resultCode)
        }
    }
}