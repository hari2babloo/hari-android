package io.scal.ambi.ui.home.chat.details

import io.reactivex.Observable
import io.scal.ambi.R
import io.scal.ambi.extensions.binding.observable.ObservableFromOtherField
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.ui.home.chat.details.data.UIChatInfo

class ChatDetailsTitleToolbarContent(viewModel: ChatDetailsViewModel) : ToolbarType.Content(R.layout.item_chat_details_toolbar, viewModel) {

    val contentInfo = ObservableFromOtherField<UIChatInfo>(
        (screenModel as ChatDetailsViewModel).dataState.toObservable().switchMap { if (null == it.chatInfo) Observable.never() else Observable.just(it.chatInfo) }
    )
}