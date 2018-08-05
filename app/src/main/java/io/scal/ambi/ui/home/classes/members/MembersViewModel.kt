package io.scal.ambi.ui.home.classes.members

import android.content.Context
import com.ambi.work.R
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.home.classes.ClassesData
import io.scal.ambi.ui.home.classes.about.*
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by chandra on 03-08-2018.
 */
class MembersViewModel @Inject internal constructor(private val context: Context, router: BetterRouter, private val interactor: IAboutInteractor,
                                                   private val rxSchedulersAbs: RxSchedulersAbs,@Named("aboutData") val memberdata: ClassesData) : AboutViewModel(context, router, interactor, rxSchedulersAbs, memberdata) {


    init {

        val fields = HashMap<String,String>()

        if(memberdata.adminIds!=null && memberdata.adminIds.size>0){
            fields.putAll(memberdata.adminIds)
        }

        if(memberdata.membersIds!=null && memberdata.membersIds.size>0){
            fields.putAll(memberdata.membersIds)
        }



        var adminsList = ArrayList<MembersData>()
        var membersList = ArrayList<MembersData>()
        if(fields.size>0){
            interactor.getUserDetailsById(fields)
                    .subscribeOn(rxSchedulersAbs.ioScheduler)
                    .observeOn(rxSchedulersAbs.computationScheduler)
                    .observeOn(rxSchedulersAbs.mainThreadScheduler)
                    .subscribe(
                            {
                                for(s:MembersData in it){
                                    if(s.id in memberdata.admins!! && s.id in memberdata.members!!){
                                        adminsList.add(s)
                                        membersList.add(s)
                                    }else if(s.id in memberdata.admins!!){
                                        adminsList.add(s)

                                    }else{
                                        membersList.add(s)
                                    }
                                }
                                var overallList = ArrayList<Any>()
                                overallList.add(MemberCount(membersList.size))
                                overallList.add(Header("admins","admins", IconImage(R.drawable.ic_profile),"admins"))
                                overallList.addAll(adminsList)
                                overallList.add(HeaderSecondary("members","members",IconImage(R.drawable.ic_profile),"members",membersList.size))
                                overallList.addAll(membersList)
                                stateModel.set(AboutDataState.AboutFeed(stateModel.get()?.profileInfo, overallList))
                            },
                            {
                                //todo
                            })
                    .addTo(disposables)
        }
    }

}