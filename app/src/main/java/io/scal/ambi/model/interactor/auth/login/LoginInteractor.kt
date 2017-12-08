package io.scal.ambi.model.interactor.auth.login

import android.content.Context
import io.reactivex.Completable
import io.scal.ambi.R
import io.scal.ambi.entity.exceptions.GoodMessageException
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.repository.auth.AuthResult
import io.scal.ambi.model.repository.auth.IAuthRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.model.repository.data.student.IStudentRepository
import javax.inject.Inject

class LoginInteractor @Inject constructor(private val context: Context,
                                          private val authRepository: IAuthRepository,
                                          private val studentRepository: IStudentRepository,
                                          private val localUserDataRepository: ILocalUserDataRepository,
                                          private val rxSchedulersAbs: RxSchedulersAbs) : ILoginInteractor {

    private val minPasswordLength = context.resources.getInteger(R.integer.min_password_length)

    override fun login(email: String, password: String): Completable =
        if (password.length < minPasswordLength) {
            Completable.error(
                GoodMessageException(context.resources.getString(R.string.error_auth_min_length, minPasswordLength)))
        } else {
            authRepository.login(email, password)
                .observeOn(rxSchedulersAbs.computationScheduler)
                .flatMap { token ->
                    studentRepository.getCurrentStudentProfile(token)
                        .map { AuthResult(token, it) }
                }
                .flatMapCompletable { localUserDataRepository.saveUserInfo(it) }
        }
}