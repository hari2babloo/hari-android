package io.scal.ambi.model.repository.data.chat

import com.twilio.chat.Channel
import com.twilio.chat.Channels
import com.twilio.chat.ChatClient
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.repository.data.chat.data.ChatChannelChanged
import io.scal.ambi.model.repository.data.chat.data.ChatChannelInfo
import io.scal.ambi.model.repository.data.chat.data.ChatClientChanged
import io.scal.ambi.model.repository.data.chat.data.ChatClientInfo
import io.scal.ambi.model.repository.data.chat.utils.*
import org.json.JSONObject
import javax.inject.Inject

class TwilioChatRepository @Inject internal constructor(authenticationRepository: TwilioAuthenticationRepository,
                                                        private val rxSchedulersAbs: RxSchedulersAbs) : IChatRepository {

    private val chatClientObservable: Observable<ChatClient> =
        authenticationRepository.getChatClientInfo()
            .switchMap {
                when (it) {
                    is ChatClientInfo.Error -> Observable.never()
                    is ChatClientInfo.Data  -> Observable.just(it.chatClient)
                }
            }

    private val chatGlobalEventsObservable: Observable<ClientEvent> =
        chatClientObservable
            .switchMap { client -> Observable.create<ClientEvent> { e -> client.setListener(NotifyCLientListener(client, e)) } }
            .retry()
            .publish()
            .refCount()

    override fun observeChatClientChanged(): Observable<ChatClientChanged> {
        return chatGlobalEventsObservable
            .flatMapMaybe { event ->
                val channel =
                    when (event) {
                        is ClientEvent.ChannelAdded   -> event.channel
                        is ClientEvent.ChannelRemoved -> event.channel
                        is ClientEvent.ChannelUpdated -> event.channel
                        else                          -> null
                    }

                channel
                    ?.waitForSync()
                    ?.flatMapMaybe { it.convertToChannelInfo(rxSchedulersAbs, null) }
                    ?.map { channelInfo ->
                        when (event) {
                            is ClientEvent.ChannelAdded   -> ChatClientChanged.ChatAdded(channelInfo)
                            is ClientEvent.ChannelRemoved -> ChatClientChanged.ChatRemoved(channelInfo)
                            is ClientEvent.ChannelUpdated -> ChatClientChanged.ChatUpdated(channelInfo)
                        }
                    }
                    ?: Maybe.empty()
            }
    }

    override fun observeChatChangedEvents(chatUids: List<String>): Observable<ChatChannelChanged> {
        return observeChatChangedEventsFromTwilio(chatUids)
            .flatMapMaybe { event ->
                event.channel.convertToChannelInfo(rxSchedulersAbs, null)
                    .flatMap { channelInfo ->
                        when (event) {
                            is ChannelEvent.MemberAdded    -> Maybe.just(ChatChannelChanged.MemberAdded(channelInfo, event.member.identity))
                            is ChannelEvent.MemberRemoved  -> Maybe.just(ChatChannelChanged.MemberRemoved(channelInfo, event.member.identity))
                            is ChannelEvent.MemberUpdated  -> Maybe.just(ChatChannelChanged.MemberUpdated(channelInfo, event.member.identity))
                            is ChannelEvent.TypingStarted  -> Maybe.just(ChatChannelChanged.TypingStarted(channelInfo, event.member.identity))
                            is ChannelEvent.TypingEnded    -> Maybe.just(ChatChannelChanged.TypingEnded(channelInfo, event.member.identity))
                            is ChannelEvent.MessageAdded   -> event.message.toChatMessage().let {
                                if (null == it) Maybe.empty() else Maybe.just(ChatChannelChanged.MessageAdded(channelInfo, it))
                            }
                            is ChannelEvent.MessageRemoved -> event.message.toChatMessage().let {
                                if (null == it) Maybe.empty() else Maybe.just(ChatChannelChanged.MessageRemoved(channelInfo, it))
                            }
                            is ChannelEvent.MessageUpdated -> event.message.toChatMessage().let {
                                if (null == it) Maybe.empty() else Maybe.just(ChatChannelChanged.MessageUpdated(channelInfo, it))
                            }
                        }
                    }
            }
    }

    private fun observeChatChangedEventsFromTwilio(chatUids: List<String>): Observable<ChannelEvent> {
        return chatClientObservable
            .switchMapSingle { client ->
                Observable
                    .fromIterable(chatUids)
                    .flatMapSingle {
                        Single.create<Channel> { e -> client.channels.getChannel(it, TwilioCallbackSingle(e, "channel info")) }
                    }
                    .flatMapSingle { it.waitForSync() }
                    .toList()
            }
            .switchMap {
                Observable.fromIterable(it)
                    .flatMap { channel -> Observable.create<ChannelEvent> { e -> channel.addListener(NotifyChannelListener(channel, e)) } }
            }
    }

    override fun loadAllChannelList(page: Int): Single<List<ChatChannelInfo>> =
        if (0 == page) {
            chatClientObservable
                .firstOrError()
                .map { it.channels.subscribedChannels }
                .flatMap {
                    Observable
                        .fromIterable(it)
                        .flatMapSingle { it.waitForSync() }
                        .flatMapMaybe { it.convertToChannelInfo(rxSchedulersAbs, null) }
                        .toList()
                }
        } else {
            Single.just(emptyList())
        }

    override fun createChat(createInfo: IChatRepository.ChatCreateInfo, currentUser: String): Single<ChatChannelInfo> {
        return chatClientObservable
            .firstOrError()
            .map { it.channels }
            .flatMap { channels ->
                findExistingChatForMembers(channels, createInfo.memberUids, currentUser)
                    .onErrorResumeNext { createNewChat(channels, createInfo, currentUser) }
            }
            .flatMap { it.convertToChannelInfo(rxSchedulersAbs, createInfo.type).toSingle() }
    }

    private fun findExistingChatForMembers(channels: Channels, memberUids: List<String>, currentUser: String): Single<Channel> {
        val allMembers = memberUids.plus(currentUser)

        return Single.just(channels.subscribedChannels)
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMapSingle { it.waitForSync() }
            .flatMapMaybe {
                if (it.members.membersList.map { it.identity } == allMembers) {
                    Maybe.just(it)
                } else {
                    Maybe.empty()
                }
            }
            .firstOrError()
    }

    private fun createNewChat(channels: Channels, createInfo: IChatRepository.ChatCreateInfo, currentUser: String): Single<Channel> {
        return Single.create<Channel> { e ->
            val attrs = JSONObject()
            if (createInfo is IChatRepository.ChatCreateInfo.Organization) {
                attrs.put("conversationType", createInfo.type.serverName)
                attrs.put("organizationName", createInfo.orgName)
                attrs.put("organizationSlug", createInfo.orgSlug)
                attrs.put("origin", createInfo.type.serverName)
            }
            attrs.put("purpose", createInfo.name)

            channels.channelBuilder()
                .withFriendlyName(createInfo.name)
                .withType(Channel.ChannelType.PRIVATE)
                .withAttributes(attrs)
                .build(TwilioCallbackSingle(e, "channelCreation"))
        }
            .flatMap { it.waitForSync() }
            .flatMap { channel ->
                Observable.fromIterable(createInfo.memberUids)
                    .startWith(currentUser)
                    .flatMapCompletable { Completable.create { e -> channel.members.addByIdentity(it, TwilioCallbackCompletable(e, "addMember")) } }
                    .andThen(Single.just(channel))
            }
    }
}