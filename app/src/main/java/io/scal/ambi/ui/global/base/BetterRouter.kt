package io.scal.ambi.ui.global.base

import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.result.ResultListener

open class BetterRouter(private val parentRouter: BetterRouter? = null) : Router() {

    private val resultListeners = mutableMapOf<Int, ResultListener?>()

    override fun setResultListener(resultCode: Int, listener: ResultListener?) {
        super.setResultListener(resultCode, listener)
        resultListeners.put(resultCode, listener)
        parentRouter?.setResultListener(resultCode, {
            sendResult(resultCode, it)
        })
    }

    @Deprecated(message = "you should use removeResultListener(resultCode: Int, listener: ResultListener) instead",
                level = DeprecationLevel.ERROR,
                replaceWith = ReplaceWith(expression = "removeResultListener(resultCode, listener)"))
    override final fun removeResultListener(resultCode: Int) {
        throw UnsupportedOperationException("you should use removeResultListener(resultCode: Int, listener: ResultListener) instead")
    }

    open fun removeResultListener(resultCode: Int, listener: ResultListener) {
        if (resultListeners[resultCode] == listener) {
            parentRouter?.removeResultListener(resultCode, listener)
            removeResultListenerInner(resultCode)
        }
    }

    private fun removeResultListenerInner(resultCode: Int) {
        resultListeners.remove(resultCode)
        super.removeResultListener(resultCode)
    }
}