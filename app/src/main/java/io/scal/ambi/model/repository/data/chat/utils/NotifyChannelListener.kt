package io.scal.ambi.model.repository.data.chat.utils

import com.twilio.chat.Channel
import com.twilio.chat.Member
import com.twilio.chat.Message
import io.reactivex.ObservableEmitter

internal class NotifyChannelListener(private val channel: Channel,
                                     private val emitter: ObservableEmitter<ChannelEvent>) : SimpleChannelListener() {

    init {
        emitter.setCancellable { channel.safeRemoveListener(this) }
    }

    override fun onMemberAdded(p0: Member) {
        super.onMemberAdded(p0)
        notifyEvent(ChannelEvent.MemberAdded(channel, p0))
    }

    override fun onMemberDeleted(p0: Member) {
        super.onMemberDeleted(p0)
        notifyEvent(ChannelEvent.MemberRemoved(channel, p0))
    }

    override fun onMemberUpdated(p0: Member, p1: Member.UpdateReason) {
        super.onMemberUpdated(p0, p1)
        notifyEvent(ChannelEvent.MemberUpdated(channel, p0))
    }

    override fun onTypingStarted(p0: Member) {
        super.onTypingStarted(p0)
        notifyEvent(ChannelEvent.TypingStarted(channel, p0))
    }

    override fun onTypingEnded(p0: Member) {
        super.onTypingEnded(p0)
        notifyEvent(ChannelEvent.TypingEnded(channel, p0))
    }

    override fun onMessageAdded(p0: Message) {
        super.onMessageAdded(p0)
        notifyEvent(ChannelEvent.MessageAdded(channel, p0))
    }

    override fun onMessageDeleted(p0: Message) {
        super.onMessageDeleted(p0)
        notifyEvent(ChannelEvent.MessageRemoved(channel, p0))
    }

    override fun onMessageUpdated(p0: Message, p1: Message.UpdateReason) {
        super.onMessageUpdated(p0, p1)
        notifyEvent(ChannelEvent.MessageUpdated(channel, p0))
    }

    private fun notifyEvent(channelEvent: ChannelEvent) {
        if (!emitter.isDisposed) {
            emitter.onNext(channelEvent)
        }
    }
}

internal sealed class ChannelEvent(val channel: Channel,
                                   val channelInfoChange: Boolean,
                                   val messageChange: Boolean) {

    class MessageAdded(channel: Channel, val message: Message) : ChannelEvent(channel, false, true)
    class MessageRemoved(channel: Channel, val message: Message) : ChannelEvent(channel, false, true)
    class MessageUpdated(channel: Channel, val message: Message) : ChannelEvent(channel, false, true)

    class MemberAdded(channel: Channel, val member: Member) : ChannelEvent(channel, true, false)
    class MemberRemoved(channel: Channel, val member: Member) : ChannelEvent(channel, true, false)
    class MemberUpdated(channel: Channel, val member: Member) : ChannelEvent(channel, true, false)

    class TypingStarted(channel: Channel, val member: Member) : ChannelEvent(channel, false, true)
    class TypingEnded(channel: Channel, val member: Member) : ChannelEvent(channel, false, true)
}