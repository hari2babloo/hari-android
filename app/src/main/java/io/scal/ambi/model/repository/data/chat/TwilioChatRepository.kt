package io.scal.ambi.model.repository.data.chat

import android.content.Context
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.repository.local.StrongRefPrefser
import javax.inject.Inject

class TwilioChatRepository @Inject internal constructor(context: Context,
                                                        private val authenticationRepository: TwilioAuthenticationRepository,
                                                        private val rxSchedulersAbs: RxSchedulersAbs) : IChatRepository {

    private val prefs = StrongRefPrefser(context.getSharedPreferences("localTwilioData", Context.MODE_PRIVATE))

    init {
        authenticationRepository.getChatClientInfo()
            .subscribe()
    }
}