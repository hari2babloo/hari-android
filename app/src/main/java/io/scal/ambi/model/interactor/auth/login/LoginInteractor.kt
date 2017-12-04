package io.scal.ambi.model.interactor.auth.login

import android.content.Context
import io.reactivex.Completable
import io.scal.ambi.R
import io.scal.ambi.entity.exceptions.GoodMessageException
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.repository.auth.IAuthRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class LoginInteractor @Inject constructor(private val context: Context,
                                          private val authRepository: IAuthRepository,
                                          private val localUserDataRepository: ILocalUserDataRepository,
                                          private val rxSchedulersAbs: RxSchedulersAbs) : ILoginInteractor {

    private val minUserNameLength = context.resources.getInteger(R.integer.min_user_name_length)
    private val minPasswordLength = context.resources.getInteger(R.integer.min_password_length)

    override fun login(userName: String, password: String): Completable {
        if (userName.length < minUserNameLength || password.length < minPasswordLength) {
            return Completable.error(GoodMessageException(context.resources.getString(R.string.error_auth_min_length, minUserNameLength, minPasswordLength)))
        } else {
            return authRepository.login(userName, password)
                    .observeOn(rxSchedulersAbs.computationScheduler)
                    .flatMapCompletable { localUserDataRepository.saveUserInfo(it) }
        }
    }
}