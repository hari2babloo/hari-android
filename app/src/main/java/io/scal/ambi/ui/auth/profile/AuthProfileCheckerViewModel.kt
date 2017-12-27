package io.scal.ambi.ui.auth.profile

import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.auth.profile.IAuthProfileCheckerInteractor
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import javax.inject.Inject

class AuthProfileCheckerViewModel @Inject constructor(router: BetterRouter,
                                                      interactor: IAuthProfileCheckerInteractor,
                                                      rxSchedulersAbs: RxSchedulersAbs) : BaseViewModel(router) {

    val userProfile: Observable<User> = PublishSubject.create()

    init {
        interactor.getUserProfile()
            .compose(rxSchedulersAbs.getIOToMainTransformer())
            .subscribe({ (userProfile as Subject).onNext(it) },
                       { handleLogout() })
            .addTo(disposables)
    }

    private fun handleLogout() {
        router.newRootScreen(NavigateTo.LOGIN)
    }
}