package io.scal.ambi.model.interactor.home.chat

import android.os.SystemClock
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.scal.ambi.R
import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.entity.chat.ChatMessage
import io.scal.ambi.entity.chat.FullChatItem
import io.scal.ambi.entity.chat.PreviewChatItem
import io.scal.ambi.entity.organization.OrganizationType
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.IconImageUser
import io.scal.ambi.model.repository.data.chat.data.ChatChannelInfo
import io.scal.ambi.model.repository.data.organization.IOrganizationRepository
import io.scal.ambi.model.repository.data.user.IUserRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import java.util.*

object ChatInfoGenerator {

    internal fun generatePreviewChat(chatInfo: ChatChannelInfo,
                                     localUserDataRepository: ILocalUserDataRepository,
                                     userRepository: IUserRepository,
                                     organizationRepository: IOrganizationRepository,
                                     rxSchedulersAbs: RxSchedulersAbs): Maybe<PreviewChatItem> {
        return generateChatUsers(chatInfo, userRepository)
            .flatMapMaybe { users ->
                if (checkChatInfo(users)) {
                    Observable
                        .combineLatest(
                            generateChatDescription(chatInfo, users, localUserDataRepository, organizationRepository, rxSchedulersAbs).toObservable(),
                            generateChatMessage(chatInfo.lastMessage, userRepository).map { it as Any }.toSingle(Unit).toObservable(),
                            BiFunction<Any, Any, Array<Any>> { t1, t2 -> arrayOf(t1, t2) }
                        )
                        .firstOrError()
                        .map { array -> generatePreviewChat(chatInfo, users, array[0] as ChatChannelDescription, array[1] as? ChatMessage) }
                        .toMaybe()
                } else {
                    Maybe.empty<PreviewChatItem>()
                }
            }
    }

    internal fun generateFullChat(chatInfo: ChatChannelInfo,
                                  localUserDataRepository: ILocalUserDataRepository,
                                  userRepository: IUserRepository,
                                  organizationRepository: IOrganizationRepository,
                                  rxSchedulersAbs: RxSchedulersAbs): Maybe<FullChatItem> {
        return generateChatUsers(chatInfo, userRepository)
            .flatMapMaybe { users ->
                if (checkChatInfo(users)) {
                    generateChatDescription(chatInfo, users, localUserDataRepository, organizationRepository, rxSchedulersAbs)
                        .flatMapMaybe { mainDescriptor ->
                            val creator = users.firstOrNull { it.uid == chatInfo.createdBy }
                            if (null == creator) {
                                Maybe.empty<FullChatItem>()
                            } else {
                                val result: FullChatItem =
                                    when (chatInfo.organization?.type) {
                                        null                       -> FullChatItem.Direct(mainDescriptor, users)
                                        OrganizationType.CLASS     -> FullChatItem.Group(mainDescriptor, listOf(mainDescriptor), creator, users)
                                        OrganizationType.GROUP     -> FullChatItem.Group(mainDescriptor, listOf(mainDescriptor), creator, users)
                                        OrganizationType.COMMUNITY -> FullChatItem.Group(mainDescriptor, listOf(mainDescriptor), creator, users)
                                    }
                                Maybe.just(result)
                            }
                        }
                } else {
                    Maybe.empty<FullChatItem>()
                }
            }
    }

    private fun generateChatDescription(chatInfo: ChatChannelInfo,
                                        users: List<User>,
                                        localUserDataRepository: ILocalUserDataRepository,
                                        organizationRepository: IOrganizationRepository,
                                        rxSchedulersAbs: RxSchedulersAbs): Single<ChatChannelDescription> {
        return generateChatIconNameFriendlyChats(chatInfo, users, localUserDataRepository, organizationRepository, rxSchedulersAbs)
            .map { ChatChannelDescription(chatInfo.uid, it.first, it.second, chatInfo.dateTime) }
    }

    private fun generateChatIconNameFriendlyChats(chatInfo: ChatChannelInfo,
                                                  users: List<User>,
                                                  localUserDataRepository: ILocalUserDataRepository,
                                                  organizationRepository: IOrganizationRepository,
                                                  rxSchedulersAbs: RxSchedulersAbs): Single<Triple<String, IconImage, List<String>>> {
        return if (null == chatInfo.organization) {
            val currentUser = localUserDataRepository.getCurrentUser()
            Observable
                .combineLatest(
                    generateChatName(chatInfo, users, currentUser).toObservable(),
                    generateChatIcon(chatInfo, users, currentUser).toObservable(),
                    BiFunction<String, IconImage, Triple<String, IconImage, List<String>>> { t1, t2 -> Triple(t1, t2, emptyList()) }
                )
                .firstOrError()
        } else {
            organizationRepository
                .loadOrganizationBySlug(chatInfo.organization)
                .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
                .map { org -> Triple(org.name, org.bannerPicture, emptyList<String>()) }
                .onErrorResumeNext(Single.just(Triple(chatInfo.organization.name, IconImage(R.drawable.ic_ambi_logo_small), emptyList())))
        }
    }

    private fun generateChatName(chatInfo: ChatChannelInfo, users: List<User>, currentUser: User?): Single<String> {
        return Single.just(users.filter { it != currentUser }.map { it.name }.fold("", { acc, name -> if (acc.isEmpty()) name else "$acc, $name" }))
    }

    private fun generateChatIcon(chatInfo: ChatChannelInfo, members: List<User>, currentUser: User?): Single<IconImage> =
        if (members.isEmpty()) {
            Maybe.empty()
        } else {
            val membersFiltered = members.filter { it != currentUser }
            val member = membersFiltered[Random(SystemClock.currentThreadTimeMillis()).nextInt(membersFiltered.size)]
            Maybe.just(member.avatar as IconImage)
        }
            .toSingle(IconImageUser())

    private fun generatePreviewChat(chatInfo: ChatChannelInfo,
                                    users: List<User>,
                                    description: ChatChannelDescription,
                                    lastMessage: ChatMessage?): PreviewChatItem {
        return when (chatInfo.organization?.type) {
            null                       -> PreviewChatItem.Direct(description,
                                                                 users,
                                                                 lastMessage,
                                                                 chatInfo.hasNewMessages)
            OrganizationType.GROUP     -> PreviewChatItem.Organization(description,
                                                                       listOf(description),
                                                                       users,
                                                                       lastMessage,
                                                                       chatInfo.hasNewMessages)
            OrganizationType.CLASS     -> PreviewChatItem.Organization(description,
                                                                       listOf(description),
                                                                       users,
                                                                       lastMessage,
                                                                       chatInfo.hasNewMessages)
            OrganizationType.COMMUNITY -> PreviewChatItem.Organization(description,
                                                                       listOf(description),
                                                                       users,
                                                                       lastMessage,
                                                                       chatInfo.hasNewMessages)
        }
    }

    private fun generateChatUsers(chatInfo: ChatChannelInfo, userRepository: IUserRepository): Single<List<User>> =
        Observable.fromIterable(chatInfo.memberUids)
            .flatMapMaybe { getUserProfile(it, userRepository) }
            .toList()

    private fun checkChatInfo(users: List<User>): Boolean =
        when {
            users.isEmpty() -> false
            else            -> true
        }
}