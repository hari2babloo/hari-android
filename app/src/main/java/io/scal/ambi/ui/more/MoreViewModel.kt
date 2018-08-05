package io.scal.ambi.ui.more

import android.content.Context
import android.databinding.ObservableField
import com.ambi.work.R
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.profile.IProfileDetailsInteractor
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import io.scal.ambi.ui.more.adapter.IMoreItemViewModel
import io.scal.ambi.ui.more.data.MoreData
import io.scal.ambi.ui.profile.details.ProfileDetailsDataState
import io.scal.ambi.ui.profile.details.UIProfile
import javax.inject.Inject

class MoreViewModel @Inject internal constructor(private val context: Context,router: BetterRouter,private val interactor: IProfileDetailsInteractor, private val rxSchedulersAbs: RxSchedulersAbs) :
        BaseViewModel(router), IMoreItemViewModel, IMoreViewModel {

    val dataState = ObservableField<ProfileDetailsDataState>()

    override fun optionSelected(position: MoreData) {
        if (position.name.equals("logout")) {
            router.navigateTo(NavigateTo.LOGOUT)
        }
    }


    override fun onViewProfileClick() {
        //todo
    }

    init {
        getProfileDetails()
    }

    fun getProfileDetails(){
        interactor.loadCurrentUser()
                .compose(rxSchedulersAbs.getIOToMainTransformer())
                .subscribe(
                        {
                            dataState.set(ProfileDetailsDataState.DataInfoOnly(it.toUIProfile(context, true)))
                        },
                        { t ->
                            //todo
                        })
                .addTo(disposables)
    }
}

private fun User.toUIProfile(context: Context, showSettings: Boolean): UIProfile {
    val workAt = workExperience?.lastOrNull()
    return UIProfile(name,
            avatar,
            banner,
            workAt?.let { "${it.title} at ${it.description}" },
            liveAt?.let { context.getString(R.string.profile_details_life_at, it) },
            showSettings
    )

}