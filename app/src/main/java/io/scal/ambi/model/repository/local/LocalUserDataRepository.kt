package io.scal.ambi.model.repository.local

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.scal.ambi.entity.User
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.repository.auth.AuthResult
import javax.inject.Inject

class LocalUserDataRepository @Inject constructor(context: Context,
                                                  private val rxSchedulersAbs: RxSchedulersAbs) : ILocalUserDataRepository {

    private val prefs = StrongRefPrefser(context.getSharedPreferences("localUserData", Context.MODE_PRIVATE))

    override fun saveUserInfo(authResult: AuthResult): Completable =
        Completable.fromAction { prefs.put(USER_INFO, authResult) }

    override fun removeUserInfo(): Completable =
        Completable.fromAction { prefs.remove(USER_INFO) }

    override fun observeUserInfo(): Observable<AuthResult> =
        observePrefs(USER_INFO, AuthResult::class.java, DEFAULT_AUTH_RESULT, true)

    override fun getUserInfo(): AuthResult? =
        prefs.get(USER_INFO, AuthResult::class.java, null)

    private fun <T> observePrefs(key: String, dataClass: Class<T>, defaultValue: T, errorOnDefault: Boolean = false): Observable<T> {
        return prefs.getAndObserve(key, dataClass, defaultValue)
            .compose(defaultToErrorComposer(dataClass, defaultValue, errorOnDefault))
    }

    private fun <T> defaultToErrorComposer(dataClass: Class<T>, defaultValue: T, errorOnDefault: Boolean): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream.flatMap {
                if (it == defaultValue) {
                    if (errorOnDefault) {
                        Observable.error(IllegalArgumentException("no value found for class ${dataClass.simpleName}"))
                    } else {
                        Observable.empty()
                    }
                } else {
                    Observable.just(it)
                }
            }
        }
    }

    companion object {

        private const val USER_INFO = "UserInfo"
        private val DEFAULT_AUTH_RESULT = AuthResult("", User())
    }
}