package io.scal.ambi.model.repository.data.chat.push

import android.content.Intent
import com.google.firebase.iid.FirebaseInstanceIdService


class FCMInstanceIDService : FirebaseInstanceIdService() {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    override fun onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes.
        val intent = Intent(this, RegistrationIntentService::class.java)
        startService(intent)
    }
}