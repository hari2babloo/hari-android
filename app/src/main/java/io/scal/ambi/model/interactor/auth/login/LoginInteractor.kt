package io.scal.ambi.model.interactor.auth.login

import android.content.Context
import android.util.Base64
import io.reactivex.Completable
import io.reactivex.Single
import com.ambi.work.R
import io.scal.ambi.entity.exceptions.GoodMessageException
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.repository.auth.IAuthRepository
import io.scal.ambi.model.repository.data.user.IUserRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import javax.inject.Inject


class LoginInteractor @Inject constructor(private val context: Context,
                                          private val authRepository: IAuthRepository,
                                          private val userRepository: IUserRepository,
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
                .flatMapCompletable { authResult ->
                    Single.fromCallable { decodePayload(authResult.token) }
                        .flatMapCompletable {
                            userRepository.extractUserProfileFromData(it)
                                .flatMapCompletable { localUserDataRepository.saveCurrentUser(it) }
                                .andThen(localUserDataRepository.saveCurrentToken(authResult.token))
                        }
                }
                .doOnError { localUserDataRepository.removeAllUserInfo().subscribe() }
        }

    private fun decodePayload(token: String): String {
        val parts = splitToken(token)
        return base64Decode(parts[1])
    }

    private fun splitToken(token: String): Array<String> {
        val parts = token.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (parts.size != 3) {
            throw IllegalArgumentException(String.format("The token was expected to have 3 parts, but got %s.", parts.size))
        }
        return parts
    }

    private fun base64Decode(string: String): String {
        try {
            val bytes = Base64.decode(string, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            return String(bytes, Charset.forName("UTF-8"))
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Received bytes didn't correspond to a valid Base64 encoded string.", e)
        } catch (e: UnsupportedEncodingException) {
            throw IllegalArgumentException("Device doesn't support UTF-8 charset encoding.", e)
        }
    }
}