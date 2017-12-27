package io.scal.ambi.ui.global.base.viewmodel

import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.ui.global.base.BetterRouter
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseUserViewModel(router: BetterRouter,
                                 private val userLoader: () -> Observable<User>,
                                 protected val rxSchedulersAbs: RxSchedulersAbs) : BaseViewModel(router) {

    val currentUser = ObservableField<User>()
    private val currentUserWasSetup = AtomicBoolean(false)

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        userLoader.invoke()
            .compose(rxSchedulersAbs.getIOToMainTransformer())
            .onErrorResumeNext(Observable.never())
            .subscribe {
                currentUser.set(it)

                if (currentUserWasSetup.compareAndSet(false, true)) {
                    onCurrentUserFetched(it)
                }
            }
            .addTo(disposables)
    }

    protected open fun onCurrentUserFetched(user: User) {}
}