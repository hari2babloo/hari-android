package io.scal.ambi.model.interactor.home.newsfeed.creation

import io.reactivex.Completable
import io.scal.ambi.entity.chat.ChatAttachment
import io.scal.ambi.entity.feed.AnnouncementType
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.model.interactor.base.IFileUploadInteractor
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.model.repository.toServerResponseException
import javax.inject.Inject

class AnnouncementCreationInteractor @Inject constructor(localUserDataRepository: ILocalUserDataRepository,
                                                         private val postsRepository: IPostsRepository,
                                                         private val fileUploadInteractor: IFileUploadInteractor) :
    BaseCreationInteractor(localUserDataRepository),
    IAnnouncementCreationInteractor {

    override val availableAudiences: List<Audience> = listOf(Audience.STUDENTS, Audience.FACULTY, Audience.STAFF)
    override val availableAnnouncementTypes: List<AnnouncementType> = listOf(AnnouncementType.GENERAL,
                                                                             AnnouncementType.SAFETY,
                                                                             AnnouncementType.TRAGEDY,
                                                                             AnnouncementType.GOOD_NEWS,
                                                                             AnnouncementType.EVENT)

    override fun createAnnouncement(announcementCreation: AnnouncementCreation): Completable {
        val audiences =
            if (announcementCreation.audiences == Audience.EVERYONE) {
                availableAudiences
            } else {
                listOf(announcementCreation.audiences)
            }

        val attachment = announcementCreation.attachments.firstOrNull { it is ChatAttachment.LocalFile || it is ChatAttachment.LocalImage }
        return if (null == attachment) {
            sendFeedItem(announcementCreation, audiences, null)
        } else {
            when (attachment) {
                is ChatAttachment.LocalFile  -> fileUploadInteractor.uploadFile(attachment.fileFile)
                is ChatAttachment.LocalImage -> fileUploadInteractor.uploadImage(attachment.imageFile, null)
                else                         -> throw IllegalArgumentException("wrong attachment type")
            }
                .flatMapCompletable { sendFeedItem(announcementCreation, audiences, it.id) }
        }
    }

    private fun sendFeedItem(announcementCreation: AnnouncementCreation,
                             audiences: List<Audience>,
                             fileId: String?): Completable {
        return postsRepository
            .postNewAnnouncement(announcementCreation.pinned,
                                 announcementCreation.locked,
                                 announcementCreation.selectedAsUser.uid,
                                 announcementCreation.announcementText,
                                 announcementCreation.announcementType,
                                 audiences,
                                 fileId
            )
            .onErrorResumeNext { t -> Completable.error(t.toServerResponseException()) }
    }

}