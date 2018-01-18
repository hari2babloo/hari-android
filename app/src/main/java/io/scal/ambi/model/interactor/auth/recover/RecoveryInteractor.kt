package io.scal.ambi.model.interactor.auth.recover

import android.content.Context
import android.support.v4.util.PatternsCompat
import io.reactivex.Completable
import com.ambi.work.R
import io.scal.ambi.entity.exceptions.GoodMessageException
import io.scal.ambi.model.repository.auth.IAuthRepository
import javax.inject.Inject

class RecoveryInteractor @Inject constructor(private val context: Context,
                                             private val authRepository: IAuthRepository) : IRecoveryInteractor {

    override fun recover(email: String): Completable =
        if (PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            authRepository.recover(email)
        } else {
            Completable.error(GoodMessageException(context.getString(R.string.error_auth_wrong_email_format)))
        }
}