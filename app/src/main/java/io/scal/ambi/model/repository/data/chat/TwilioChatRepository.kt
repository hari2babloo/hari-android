package io.scal.ambi.model.repository.data.chat

import com.twilio.chat.ChatClient
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
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

    private val allChannelsPaginator = TwilioUserChannelsPaginator(chatClientObservable, rxSchedulersAbs)

    override fun loadAllChannelList(page: Int): Single<List<ChatChannelInfo>> =
        allChannelsPaginator.loadNextPage(page)
//        loadAllSubscribedChannelList()

    private fun loadAllSubscribedChannelList(): Single<List<ChatChannelInfo>> {
        return chatClientObservable
            .firstOrError()
            .map { it.channels.subscribedChannels }
            .flatMap {
                Observable.fromIterable(it)
                    .flatMap { it.convertToChannelInfo(rxSchedulersAbs, null) }
                    .toList()
            }
    }
}