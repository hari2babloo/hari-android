package io.scal.ambi.model.repository.local

import android.content.Context
import java.util.*
import javax.inject.Inject

private const val DEVICE_UID = "deviceUid"

class LocalDataRepository @Inject constructor(context: Context) : ILocalDataRepository {

    private val prefs = StrongRefPrefser(context.getSharedPreferences("localData", Context.MODE_PRIVATE))

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
}