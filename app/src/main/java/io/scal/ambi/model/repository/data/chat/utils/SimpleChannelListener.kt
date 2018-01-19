package io.scal.ambi.model.repository.data.chat.utils

import com.twilio.chat.Channel
import com.twilio.chat.ChannelListener
import com.twilio.chat.Member
import com.twilio.chat.Message

internal open class SimpleChannelListener : ChannelListener {

    override fun onMemberAdded(p0: Member) {}
    override fun onMemberDeleted(p0: Member) {}
    override fun onMemberUpdated(p0: Member, p1: Member.UpdateReason) {}


    override fun onTypingStarted(p0: Member) {}
    override fun onTypingEnded(p0: Member) {}


    override fun onMessageAdded(p0: Message) {}
    override fun onMessageDeleted(p0: Message) {}
    override fun onMessageUpdated(p0: Message, p1: Message.UpdateReason) {}


    override fun onSynchronizationChanged(p0: Channel) {}
}