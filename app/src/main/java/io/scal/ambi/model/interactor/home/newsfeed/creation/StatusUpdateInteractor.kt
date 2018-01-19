package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Completable
import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.model.interactor.base.IFileUploadInteractor
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.model.repository.toServerResponseException
import javax.inject.Inject

class StatusUpdateInteractor @Inject constructor(localUserDataRepository: ILocalUserDataRepository,
                                                 private val postsRepository: IPostsRepository,
                                                 private val fileUploadInteractor: IFileUploadInteractor) :
    BaseCreationInteractor(localUserDataRepository),
    IStatusUpdateInteractor {

    override val availableAudiences: List<Audience> = listOf(Audience.STUDENTS, Audience.FACULTY, Audience.STAFF)

    override fun updateStatus(statusUpdate: StatusUpdate): Completable {
        val audiences =
            if (statusUpdate.audiences == Audience.EVERYONE) {
                availableAudiences
            } else {
                listOf(statusUpdate.audiences)
            }

        val attachment = statusUpdate.attachments.firstOrNull { it is ChatAttachment.LocalImage || it is ChatAttachment.LocalFile }
        return if (null == attachment) {
            sendFeedItem(statusUpdate, audiences, null)
        } else {
            when (attachment) {
                is ChatAttachment.LocalFile  -> fileUploadInteractor.uploadFile(attachment.fileFile)
                is ChatAttachment.LocalImage -> fileUploadInteractor.uploadImage(attachment.imageFile)
                else                         -> throw IllegalArgumentException("wrong attachment type")
            }
                .flatMapCompletable { sendFeedItem(statusUpdate, audiences, it.id) }
        }
    }

    private fun sendFeedItem(statusUpdate: StatusUpdate,
                             audiences: List<Audience>,
                             fileId: String?): Completable {
        return postsRepository
            .postNewStatus(statusUpdate.pinned,
                           statusUpdate.locked,
                           statusUpdate.selectedAsUser.uid,
                           statusUpdate.statusText,
                           audiences,
                           fileId
            )
            .onErrorResumeNext { t -> Completable.error(t.toServerResponseException()) }
    }
}