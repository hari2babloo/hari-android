package io.scal.ambi.model.interactor.home.chat

import android.os.SystemClock
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.scal.ambi.R
import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.entity.chat.ChatMessage
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

object ChatItemGenerator {

    internal fun generateChatItem(chatInfo: ChatChannelInfo,
                                  localUserDataRepository: ILocalUserDataRepository,
                                  userRepository: IUserRepository,
                                  organizationRepository: IOrganizationRepository,
                                  rxSchedulersAbs: RxSchedulersAbs): Maybe<PreviewChatItem> {
        return generateChatUsers(chatInfo, userRepository)
            .flatMapMaybe { users ->
                if (checkChatInfo(users)) {
                    Observable
                        .combineLatest(
                            generateChatIconAndName(chatInfo, users, localUserDataRepository, organizationRepository, rxSchedulersAbs).toObservable(),
                            generateChatMessage(chatInfo.lastMessage, userRepository).map { it as Any }.toSingle(Unit).toObservable(),
                            BiFunction<Any, Any, Array<Any>> { t1, t2 ->
                                @Suppress("UNCHECKED_CAST")
                                t1 as Pair<String, IconImage>
                                arrayOf(t1.first, t1.second, t2)
                            }
                        )
                        .firstOrError()
                        .map { array ->
                            generateChatItem(chatInfo, users, array[0] as String, array[1] as IconImage, array[2] as? ChatMessage)
                        }
                        .toMaybe()
                } else {
                    Maybe.empty<PreviewChatItem>()
                }
            }
    }

    private fun generateChatIconAndName(chatInfo: ChatChannelInfo,
                                        users: List<User>,
                                        localUserDataRepository: ILocalUserDataRepository,
                                        organizationRepository: IOrganizationRepository,
                                        rxSchedulersAbs: RxSchedulersAbs): Single<Pair<String, IconImage>> {
        return if (null == chatInfo.organization) {
            val currentUser = localUserDataRepository.getCurrentUser()
            Observable
                .combineLatest(
                    generateChatName(chatInfo, users, currentUser).toObservable(),
                    generateChatIcon(chatInfo, users, currentUser).toObservable(),
                    BiFunction<String, IconImage, Pair<String, IconImage>> { t1, t2 -> Pair(t1, t2) }
                )
                .firstOrError()
        } else {
            organizationRepository
                .loadOrganizationBySlug(chatInfo.organization)
                .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
                .map { org -> Pair(org.name, org.bannerPicture) }
                .onErrorResumeNext(Single.just(Pair(chatInfo.organization.name, IconImage(R.drawable.ic_ambi_logo_small))))
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

    private fun generateChatItem(chatInfo: ChatChannelInfo,
                                 users: List<User>,
                                 name: String,
                                 icon: IconImage,
                                 lastMessage: ChatMessage?): PreviewChatItem {
        val description = ChatChannelDescription(chatInfo.uid, name, icon, chatInfo.dateTime)
        return when (chatInfo.organization?.type) {
            null                   -> PreviewChatItem.Direct(description,
                                                             icon,
                                                             users,
                                                             lastMessage,
                                                             chatInfo.hasNewMessages)
            OrganizationType.GROUP -> PreviewChatItem.Group(description,
                                                            listOf(description),
                                                            icon,
                                                            users,
                                                            lastMessage,
                                                            chatInfo.hasNewMessages)
            OrganizationType.CLASS -> PreviewChatItem.Group(description,
                                                            listOf(description),
                                                            icon,
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