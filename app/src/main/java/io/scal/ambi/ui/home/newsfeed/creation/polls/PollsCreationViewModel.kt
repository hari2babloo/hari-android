package io.scal.ambi.ui.home.newsfeed.creation.polls

import android.databinding.ObservableField
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.binding.observable.ObservableString
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.home.newsfeed.creation.IPollsCreationInteractor
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import org.joda.time.Duration
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class PollsCreationViewModel @Inject constructor(router: Router,
                                                 interactor: IPollsCreationInteractor,
                                                 rxSchedulersAbs: RxSchedulersAbs) : BaseViewModel(router) {

    val stateModel = ObservableField<PollsCreationStateModel>(PollsCreationStateModel.EmptyProgress())

    init {
        interactor
            .loadAsUsers()
            .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
            .subscribe({
                           stateModel.set(PollsCreationStateModel.Data(false,
                                                                       false,
                                                                       it[0],
                                                                       ObservableString(""),
                                                                       emptyList(),
                                                                       Duration.standardDays(7)
                           ))
                       },
                       { t ->
                           handleError(t)
                           stateModel.set(PollsCreationStateModel.ErrorFatal(t.message!!))
                       })
            .addTo(disposables)
    }
}