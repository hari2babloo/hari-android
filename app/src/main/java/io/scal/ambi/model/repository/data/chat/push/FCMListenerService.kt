package io.scal.ambi.model.repository.data.chat.push

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import com.ambi.work.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.twilio.chat.NotificationPayload
import io.scal.ambi.App
import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.ui.home.chat.details.ChatDetailsActivity
import io.scal.ambi.ui.home.root.HomeActivity
import org.joda.time.DateTime
import org.json.JSONObject
import timber.log.Timber


class FCMListenerService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        if (null == remoteMessage) return

        Timber.e("!!!!!!!!!!!!!!!!")


        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            val obj = JSONObject(remoteMessage.data)

            val data = Bundle()
            data.putString("channel_id", obj.optString("channel_id"))
            data.putString("message_id", obj.optString("message_id"))
            data.putString("author", obj.optString("author"))
            data.putString("message_sid", obj.optString("message_sid"))
            data.putString("twi_sound", obj.optString("twi_sound"))
            data.putString("twi_message_type", obj.optString("twi_message_type"))
            data.putString("channel_sid", obj.optString("channel_sid"))
            data.putString("twi_message_id", obj.optString("twi_message_id"))
            data.putString("twi_body", obj.optString("twi_body"))
            data.putString("channel_title", obj.optString("channel_title"))

            val payload = NotificationPayload(data)

            val chatRepository = (applicationContext as App).chatRepository
            chatRepository.handleNotification(payload)

            val type = payload.type

            if (type == NotificationPayload.Type.UNKNOWN) return  // Ignore everything we don't support

            var title = "Notification"

            if (type == NotificationPayload.Type.NEW_MESSAGE)
                title = "New Message"
            if (type == NotificationPayload.Type.ADDED_TO_CHANNEL)
                title = "Added to Channel"
            if (type == NotificationPayload.Type.INVITED_TO_CHANNEL)
                title = "Invited to Channel"
            if (type == NotificationPayload.Type.REMOVED_FROM_CHANNEL)
                title = "Removed from Channel"

            // Set up action Intent
            val cSid = payload.channelSid
            if (ChatDetailsActivity.RUNNING_CHAT == cSid) {
                return
            }

            val intent =
                if ("".contentEquals(cSid)) {
                    HomeActivity.createScreen(this)
                } else {
                    ChatDetailsActivity.createScreen(this, ChatChannelDescription(cSid, "", IconImage(R.drawable.ic_profile), DateTime.now()))
                }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val notification = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_ambi_logo)
                .setContentTitle(title)
                .setContentText(payload.body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setColor(Color.rgb(214, 10, 37))
                .build()

            val soundFileName = payload.sound
            if (resources.getIdentifier(soundFileName, "raw", packageName) != 0) {
                val sound = Uri.parse("android.resource://$packageName/raw/$soundFileName")
                notification.defaults = notification.defaults and Notification.DEFAULT_SOUND.inv()
                notification.sound = sound
            } else {
                notification.defaults = notification.defaults or Notification.DEFAULT_SOUND
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(0, notification)
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Timber.d("Notification Message Body: " + remoteMessage.notification!!.body)
        }
    }
}