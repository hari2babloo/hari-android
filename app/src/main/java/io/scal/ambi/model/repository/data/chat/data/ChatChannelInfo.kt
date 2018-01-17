package io.scal.ambi.model.repository.data.chat.data

import io.scal.ambi.entity.organization.OrganizationType
import org.joda.time.DateTime

data class ChatChannelInfo(val uid: String,
                           val createdBy: String,
                           val organization: OrganizationSmall?,
                           val lastMessage: ChatChannelMessage?,
                           val dateTime: DateTime,
                           val hasNewMessages: Boolean,
                           val memberUids: List<String>) {

    class OrganizationSmall(val type: OrganizationType, val slug: String, val name: String)
}