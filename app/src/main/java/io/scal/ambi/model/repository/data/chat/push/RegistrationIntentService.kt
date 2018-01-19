package io.scal.ambi.model.repository.data.chat.push

import android.app.IntentService
import android.content.Intent
import com.google.firebase.iid.FirebaseInstanceId
import io.scal.ambi.App


class RegistrationIntentService : IntentService("RegistrationIntentService") {


    override fun onHandleIntent(intent: Intent?) {
        try {
            val localDataRepository = (applicationContext as App).localDataRepository

            localDataRepository.putFirebaseToken(FirebaseInstanceId.getInstance().token.orEmpty())
        } catch (e: Exception) {
            // pass
        }
    }
}