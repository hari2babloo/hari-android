package io.scal.ambi.ui.home.newsfeed.audience

import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.navigation.ResultCodes
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject
import javax.inject.Named

class AudienceSelectionViewModel @Inject constructor(router: Router,
                                                     @Named("selectedAudience") val selectedAudience: Audience) :
    BaseViewModel(router) {

    val audienceList = listOf(Audience.COLLEGE_UPDATE,
                              Audience.STUDENTS,
                              Audience.FACULTY,
                              Audience.STAFF,
                              Audience.GROUPS,
                              Audience.CLASSES,
                              Audience.COMMUNITIES,
                              Audience.NEWS)

    fun selectAudience(audience: Audience) {
        if (selectedAudience == audience) {
            router.exit()
        } else {
            router.exitWithResult(ResultCodes.AUDIENCE_SELECTION, audience)
        }
    }
}