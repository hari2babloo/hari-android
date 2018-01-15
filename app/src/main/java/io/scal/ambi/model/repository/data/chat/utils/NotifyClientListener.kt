package io.scal.ambi.model.repository.data.chat.utils

import com.twilio.chat.*
import io.reactivex.ObservableEmitter

internal class NotifyCLientListener(private val client: ChatClient,
                                    private val emitter: ObservableEmitter<ClientEvent>) : ChatClientListener {

    init {
        emitter.setCancellable { client.removeListener() }
    }

    override fun onChannelDeleted(p0: Channel) {
        notifyEvent(ClientEvent.ChannelRemoved(client, p0))
    }

    override fun onClientSynchronization(p0: ChatClient.SynchronizationStatus) {}

    override fun onNotificationSubscribed() {}

    override fun onUserSubscribed(p0: User) {}

    override fun onChannelUpdated(p0: Channel, p1: Channel.UpdateReason) {
        notifyEvent(ClientEvent.ChannelUpdated(client, p0))
    }

    override fun onNotificationFailed(p0: ErrorInfo) {}

    override fun onChannelJoined(p0: Channel) {
        notifyEvent(ClientEvent.ChannelAdded(client, p0))
    }

    override fun onChannelAdded(p0: Channel) {
        notifyEvent(ClientEvent.ChannelAdded(client, p0))
    }

    override fun onChannelSynchronizationChange(p0: Channel) {}

    override fun onNotification(p0: String, p1: String) {}

    override fun onUserUnsubscribed(p0: User) {}

    override fun onChannelInvited(p0: Channel) {}

    override fun onConnectionStateChange(p0: ChatClient.ConnectionState) {}

    override fun onError(p0: ErrorInfo) {}

    override fun onUserUpdated(p0: User, p1: User.UpdateReason) {}

    private fun notifyEvent(channelEvent: ClientEvent) {
        if (!emitter.isDisposed) {
            emitter.onNext(channelEvent)
        }
    }
}

sealed class ClientEvent(val client: ChatClient) {

    class ChannelAdded(client: ChatClient, val channel: Channel) : ClientEvent(client)
    class ChannelRemoved(client: ChatClient, val channel: Channel) : ClientEvent(client)
    class ChannelUpdated(client: ChatClient, val channel: Channel) : ClientEvent(client)
}