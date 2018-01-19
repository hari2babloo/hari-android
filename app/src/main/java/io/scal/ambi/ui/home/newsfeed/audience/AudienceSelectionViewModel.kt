package io.scal.ambi.ui.home.newsfeed.audience

import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.navigation.ResultCodes
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import javax.inject.Inject
import javax.inject.Named

class AudienceSelectionViewModel @Inject constructor(router: BetterRouter,
                                                     @Named("selectedAudience") val selectedAudience: Audience) :
    BaseViewModel(router) {

    val audienceList = listOf(Audience.STUDENTS,
                              Audience.FACULTY,
                              Audience.STAFF)

    fun selectAudience(audience: Audience) {
        if (selectedAudience == audience) {
            router.exit()
        } else {
            router.exitWithResult(ResultCodes.AUDIENCE_SELECTION, audience)
        }
    }
}