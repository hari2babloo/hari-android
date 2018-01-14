package io.scal.ambi.model.repository.data.chat

import io.reactivex.Single

interface IChatRepository {

    fun loadAllChannelList(page: Int): Single<List<ChatChannelInfo>>
}