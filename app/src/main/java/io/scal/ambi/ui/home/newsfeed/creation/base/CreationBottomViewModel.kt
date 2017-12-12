package io.scal.ambi.ui.home.newsfeed.creation.base

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class CreationBottomViewModel @Inject constructor(router: Router) : BaseViewModel(router) {

    val audienceList = listOf(Audience.EVERYONE,
                              Audience.COLLEGE_UPDATE,
                              Audience.STUDENTS,
                              Audience.FACULTY,
                              Audience.STAFF,
                              Audience.GROUPS,
                              Audience.CLASSES,
                              Audience.COMMUNITIES,
                              Audience.NEWS)
    val selectedAudience = ObservableField<Audience>(Audience.EVERYONE)
    val postEnable = ObservableBoolean(false)

    val postAction: Observable<Any> = PublishSubject.create()

    fun post() {
        (postAction as Subject).onNext(Any())
    }
}