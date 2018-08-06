package io.scal.ambi.ui.home.classes.about

import android.content.Context
import android.databinding.ObservableField
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import io.scal.ambi.ui.home.classes.ClassesData
import io.scal.ambi.ui.home.classes.members.IMembersViewModel
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by chandra on 03-08-2018.
 */
open class AboutViewModel @Inject internal constructor(private val context: Context, router: BetterRouter, private val interactor: IAboutInteractor,
                                                  private val rxSchedulersAbs: RxSchedulersAbs,@Named("aboutData") val aboutData: ClassesData): BaseViewModel(router),IAboutViewModel, IMembersViewModel {

    override fun openActionSheet(element: MembersData) {
        //todo
    }


    val stateModel = ObservableField<AboutDataState>()

    init {
        if(aboutData.adminIds!=null && aboutData.adminIds.size>0){
            interactor.getUserDetailsById(aboutData.adminIds!!)
                    .subscribeOn(rxSchedulersAbs.ioScheduler)
                    .observeOn(rxSchedulersAbs.computationScheduler)
                    .observeOn(rxSchedulersAbs.mainThreadScheduler)
                    .subscribe(
                            {
                                stateModel.set(AboutDataState.AboutFeed(stateModel.get()?.profileInfo, it))
                            },
                            {
                                //todo
                            })
                    .addTo(disposables)
        }
    }


}