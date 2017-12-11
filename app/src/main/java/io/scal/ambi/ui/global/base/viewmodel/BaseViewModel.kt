package io.scal.ambi.ui.global.base.viewmodel

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import ru.terrakok.cicerone.Router
import timber.log.Timber

abstract class BaseViewModel(protected val router: Router) : ViewModel() {

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