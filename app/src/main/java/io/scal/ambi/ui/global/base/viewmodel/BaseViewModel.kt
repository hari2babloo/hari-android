package io.scal.ambi.ui.global.base.viewmodel

import android.arch.lifecycle.ViewModel
import android.content.Context
import io.reactivex.disposables.CompositeDisposable
import com.ambi.work.R
import io.scal.ambi.model.data.server.ServerResponseException
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.BetterRouter
import timber.log.Timber

abstract class BaseViewModel(protected val router: BetterRouter) : ViewModel() {

    protected val disposables: CompositeDisposable = CompositeDisposable()

    protected fun handleError(throwable: Throwable, doLogoutAction: Boolean = true) {
        Timber.e(throwable)
        if (doLogoutAction && throwable is ServerResponseException && (throwable.requiresLogin || throwable.notFound || throwable.notAuthorized)) {
            router.newRootScreen(NavigateTo.LOGIN)
        }
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