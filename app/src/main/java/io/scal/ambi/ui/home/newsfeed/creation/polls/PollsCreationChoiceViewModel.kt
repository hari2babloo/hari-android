package io.scal.ambi.ui.home.newsfeed.creation.polls

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.extensions.binding.toObservable

class PollsCreationChoiceViewModel(startText: String,
                                   val index: Int) : ViewModel() {

    val choiceInput = ObservableString(startText)

    fun validate(): Observable<Boolean> {
        return choiceInput.data
            .toObservable()
            .map { it.isNotBlank() }
    }
}