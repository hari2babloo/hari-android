package io.scal.ambi.model.repository.data.chat

import com.twilio.chat.CallbackListener
import com.twilio.chat.ChannelDescriptor
import com.twilio.chat.ChatClient
import com.twilio.chat.Paginator
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs

internal class TwilioUserChannelsPaginator(private val chatClientObservable: Observable<ChatClient>,
                                           private val rxSchedulersAbs: RxSchedulersAbs) {

    private val allChannelPaginator = TwilioPaginator({ createChatChannelPaginator() }, { it.convertToChannelInfo(rxSchedulersAbs) }, rxSchedulersAbs)

    fun loadNextPage(page: Int): Single<List<ChatChannelInfo>> =
        allChannelPaginator.loadPage(page)

    private fun createChatChannelPaginator(): Single<Paginator<ChannelDescriptor>> =
        chatClientObservable
            .firstOrError()
            .flatMap { chatClient ->
                Single.create<Paginator<ChannelDescriptor>> { e ->
                    val listener = object : CallbackListener<Paginator<ChannelDescriptor>>() {
                        override fun onSuccess(p0: Paginator<ChannelDescriptor>) {
                            if (!e.isDisposed) {
                                e.onSuccess(p0)
                            }
                        }

                    }
                    chatClient.channels.getUserChannelsList(listener)
                }
            }
}