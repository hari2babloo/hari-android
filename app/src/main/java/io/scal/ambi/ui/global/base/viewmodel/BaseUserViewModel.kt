package io.scal.ambi.ui.global.base.viewmodel

import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.User
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import ru.terrakok.cicerone.Router
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseUserViewModel(router: Router,
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