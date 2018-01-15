package io.scal.ambi.model.repository.data.chat.utils

import com.twilio.chat.ChannelDescriptor
import com.twilio.chat.ChatClient
import com.twilio.chat.Paginator
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.repository.data.chat.data.ChatChannelInfo

internal class TwilioUserChannelsPaginator(private val chatClientObservable: Observable<ChatClient>,
                                           private val rxSchedulersAbs: RxSchedulersAbs) {

    private val allChannelPaginator = TwilioPaginator({ createChatChannelPaginator() },
                                                      { it.convertToChannelInfo(rxSchedulersAbs).toObservable() },
                                                      rxSchedulersAbs)

    fun loadNextPage(page: Int): Single<List<ChatChannelInfo>> =
        allChannelPaginator.loadPage(page)

    private fun createChatChannelPaginator(): Single<Paginator<ChannelDescriptor>> =
        chatClientObservable
            .firstOrError()
            .flatMap { chatClient ->
                Single.create<Paginator<ChannelDescriptor>> { e ->
                    chatClient.channels.getUserChannelsList(TwilioCallbackSingle(e, "userChannelsList"))
                }
            }
}