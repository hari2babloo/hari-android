package io.scal.ambi.ui.global.base.viewmodel

import android.arch.lifecycle.ViewModel
import android.content.Context
import io.reactivex.disposables.CompositeDisposable
import io.scal.ambi.R
import io.scal.ambi.ui.global.base.BetterRouter
import timber.log.Timber

abstract class BaseViewModel(protected val router: BetterRouter) : ViewModel() {

    protected val disposables: CompositeDisposable = CompositeDisposable()

    protected fun handleError(throwable: Throwable) {
        Timber.e(throwable)
    }

    open fun onBackPressed(): Boolean {
        router.exit()
        return true
    }

    override fun onCleared() {
        disposables.dispose()

        super.onCleared()
    }
}

fun Throwable.toGoodUserMessage(context: Context): String {
    return message ?: context.getString(R.string.error_unknown)
}