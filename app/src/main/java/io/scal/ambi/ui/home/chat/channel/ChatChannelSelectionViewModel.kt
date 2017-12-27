package io.scal.ambi.ui.home.chat.channel

import android.databinding.ObservableField
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.chat.ChatChannelDescription
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.navigation.ResultCodes
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class ChatChannelSelectionViewModel @Inject constructor(router: BetterRouter,
                                                        @Named("selectedChatDescription") private val selectedChat: ChatChannelDescription,
                                                        @Named("allChatDescriptions") private val allChats: List<ChatChannelDescription>,
                                                        private val rxSchedulersAbs: RxSchedulersAbs)
    : BaseViewModel(router) {

    internal val progressState = ObservableField<ChatChannelSelectionProgressState>(ChatChannelSelectionProgressState.NoProgress)
    internal val errorState = ObservableField<ChatChannelSelectionErrorState>(ChatChannelSelectionErrorState.NoErrorState)
    internal val dataState = ObservableField<ChatChannelSelectionDataState>()

    init {
        loadMainInfo()
    }

    fun loadMainInfo() {
        if (progressState.get() is ChatChannelSelectionProgressState.NoProgress) {
            progressState.set(ChatChannelSelectionProgressState.Progress)

            Single.fromCallable { allChats.map { UIChatChannelDescription(it.iconImage, it.title, it == selectedChat, it) } }
                .map { ChatChannelSelectionDataState.Data(it) }
                .delay(2, TimeUnit.SECONDS)
                .compose(rxSchedulersAbs.getComputationToMainTransformerSingle())
                .subscribe(Consumer {
                    progressState.set(ChatChannelSelectionProgressState.NoProgress)
                    dataState.set(it)
                })
                .addTo(disposables)
        }
    }

    fun selectChannel(element: UIChatChannelDescription) {
        router.exitWithResult(ResultCodes.CHAT_CHANNEL_SELECTION, element.channelDescription)
    }
}