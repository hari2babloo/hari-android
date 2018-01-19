package io.scal.ambi.model.repository.local

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.scal.ambi.entity.user.User
import javax.inject.Inject

class LocalUserDataRepository @Inject constructor(context: Context) : ILocalUserDataRepository {

    private val prefs = StrongRefPrefser(context.getSharedPreferences("localUserData", Context.MODE_PRIVATE))

    override fun saveCurrentUser(user: User): Completable =
        Completable.fromAction { prefs.put(USER_INFO, user) }

    override fun removeAllUserInfo(): Completable =
        Completable.fromAction {
            prefs.remove(USER_INFO)
            prefs.remove(USER_TOKEN)
        }

    override fun observeCurrentUser(): Observable<User> =
        observePrefs(USER_INFO, User::class.java, DEFAULT_AUTH_RESULT, true)

    override fun getCurrentUser(): User? =
        prefs.get(USER_INFO, User::class.java, null)

    override fun saveCurrentToken(token: String): Completable =
        Completable.fromAction { prefs.put(USER_TOKEN, token) }

    override fun getCurrentToken(): String? =
        prefs.get(USER_TOKEN, String::class.java, null)

    private fun <T> observePrefs(key: String, dataClass: Class<T>, defaultValue: T?, errorOnDefault: Boolean = false): Observable<T> {
        return prefs.getAndObserve(key, dataClass, defaultValue)
            .compose(defaultToErrorComposer(dataClass, defaultValue, errorOnDefault))
    }

    private fun <T> defaultToErrorComposer(dataClass: Class<T>, defaultValue: T?, errorOnDefault: Boolean): ObservableTransformer<T, T> {
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
        private const val USER_TOKEN = "UserToken"

        private val DEFAULT_AUTH_RESULT: User? = null
    }
}