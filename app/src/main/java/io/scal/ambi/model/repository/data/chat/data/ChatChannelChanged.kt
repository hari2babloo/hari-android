package io.scal.ambi.model.repository.data.chat.data

sealed class ChatClientChanged {

    class ChatAdded(val channelInfo: ChatChannelInfo) : ChatClientChanged()
    class ChatRemoved(val channelInfo: ChatChannelInfo) : ChatClientChanged()
    class ChatUpdated(val channelInfo: ChatChannelInfo) : ChatClientChanged()
}

sealed class ChatChannelChanged(val channelInfo: ChatChannelInfo) {

    class MessageAdded(channelInfo: ChatChannelInfo, val message: ChatChannelMessage) : ChatChannelChanged(channelInfo)
    class MessageRemoved(channelInfo: ChatChannelInfo, val message: ChatChannelMessage) : ChatChannelChanged(channelInfo)
    class MessageUpdated(channelInfo: ChatChannelInfo, val message: ChatChannelMessage) : ChatChannelChanged(channelInfo)

    class MemberAdded(channelInfo: ChatChannelInfo, val member: String) : ChatChannelChanged(channelInfo)
    class MemberRemoved(channelInfo: ChatChannelInfo, val member: String) : ChatChannelChanged(channelInfo)
    class MemberUpdated(channelInfo: ChatChannelInfo, val member: String) : ChatChannelChanged(channelInfo)

    class TypingStarted(channelInfo: ChatChannelInfo, val member: String) : ChatChannelChanged(channelInfo)
    class TypingEnded(channelInfo: ChatChannelInfo, val member: String) : ChatChannelChanged(channelInfo)
}
