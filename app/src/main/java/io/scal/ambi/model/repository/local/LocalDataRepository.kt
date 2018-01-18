package io.scal.ambi.model.repository.local

import android.content.Context
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.user.User
import java.io.Serializable
import java.util.*
import javax.inject.Inject

private const val DEVICE_UID = "deviceUid"
private const val PUSH_TOKEN = "pushToken"

class LocalDataRepository @Inject constructor(context: Context) : ILocalDataRepository {

    private val prefs = StrongRefPrefser(context.getSharedPreferences("localData", Context.MODE_PRIVATE))
    private val userPrefs = StrongRefPrefser(context.getSharedPreferences("localDataCachedUsers", Context.MODE_PRIVATE))

    private val guard = Object()

    override fun getDeviceUid(): String {
        synchronized(guard) {
            var value = prefs.get(DEVICE_UID, String::class.java, null)
            if (null == value) {
                value = UUID.randomUUID().toString()
                prefs.put(DEVICE_UID, value)
            }
            return value
        }
    }

    override fun putFirebaseToken(token: String) {
        prefs.put(PUSH_TOKEN, token)
    }

    override fun observeFirebaseToken(): Observable<String> {
        return prefs.getAndObserve(PUSH_TOKEN, String::class.java, "")
    }

    override fun getUserProfile(userUid: String, maxProfileLifeTimeMillis: Long): Single<User> {
        return Single
            .fromCallable {
                userPrefs.get(userUid, UserHolder::class.java, null) ?: throw IllegalStateException("user not found")
            }
            .map {
                if (it.createTime + maxProfileLifeTimeMillis > Date().time) {
                    it.user
                } else {
                    throw IllegalStateException("user cache time ends")
                }
            }
    }

    override fun putUserProfile(user: User) {
        userPrefs.put(user.uid, UserHolder(user))
    }

    private class UserHolder(val user: User, val createTime: Long = Date().time) : Serializable
}