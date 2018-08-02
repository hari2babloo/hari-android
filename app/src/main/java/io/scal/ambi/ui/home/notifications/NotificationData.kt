package io.scal.ambi.ui.home.notifications

import com.ambi.work.R
import io.scal.ambi.extensions.binding.binders.toFrescoImagePath
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.data.server.responses.organization.ItemOrganization
import io.scal.ambi.model.data.server.responses.user.ItemUser
import org.joda.time.DateTime

data class NotificationData(val type: String?,val from: ItemUser?, val fromOrganization: ItemOrganization?, val timeOfEvent: String?, val createdAt: String?, val category: String?, val isRead: Boolean?){

    val avatar = if(from!=null) from!!.parseAvatar() else IconImageUser(R.drawable.ic_profile.toFrescoImagePath())
    val firstNameLastName: String =
            if(from!=null) "${from!!.firstName!!.toCapsLetter()} ${from!!.lastName!!.toCapsLetter()}".trim()
            else
                if(fromOrganization!=null) fromOrganization!!.name!! else "Organization null"

    val time = if(createdAt!=null) createdAt.toDateTime("createdAt") else timeOfEvent.toDateTime("timeOfEvent")

    val message: String =
            when(type){
                Type.commentedPost.notificationType -> firstNameLastName + " has commented on your post"
                Type.likedPost.notificationType -> firstNameLastName + " has liked your post"
                Type.newFollower.notificationType -> firstNameLastName + " has followed you"
                Type.followedUserPost.notificationType -> firstNameLastName + " posted on the newsfeed"
                Type.wallPost.notificationType -> firstNameLastName + " has posted on your wall"
                Type.newNoteAdded.notificationType -> firstNameLastName + " has added a note to the notebook"
                Type.newFileAdded.notificationType -> firstNameLastName + " has added a file to the folder"
                Type.newPostOnGroup.notificationType -> firstNameLastName + " has posted to the group"
                Type.newPostOnCommunity.notificationType -> firstNameLastName + " has posted to the community"
                Type.newPostOnClass.notificationType -> firstNameLastName + " has posted to the class"
                Type.requestToJoinGroup.notificationType -> firstNameLastName + " has requested to join group"
                Type.invitedGroup.notificationType -> firstNameLastName + " has invited to group"
                else -> "Nothing"
            }

    enum class Type(val notificationType: String) {
        commentedPost("commented-post"),
        likedPost("liked-post"),
        newPostOnClass("new-post-on-class"),
        newPostOnGroup("new-post-on-group"),
        newNoteAdded("new-note-added"),
        newFileAdded("new-file-added"),
        newPostOnCommunity("new-post-on-community"),
        wallPost("wall-post"),
        newFollower("new-follower"),
        followedUserPost("followed-user-post"),
        requestToJoinGroup("request-to-join-group"),
        invitedGroup("invited-group")
    }

    enum class Category(val notificationCategory: String){
        group("group"),
        classes("class"),
        individual("individual")
    }

    private fun String.toCapsLetter(): String =
            if (this.length > 1) {
                substring(0, 1).toUpperCase() + substring(1)
            } else {
                this
            }

    private fun String?.toDateTime(fieldName: String): DateTime {
        val notNullString = notNullOrThrow(fieldName)
        return DateTime.parse(notNullString)
    }
}

