package io.scal.ambi.ui.home.classes.about

import io.reactivex.Single

/**
 * Created by chandra on 30-07-2018.
 */
interface IAboutInteractor {

    fun getUserDetailsById(userids: Map<String,String>): Single<List<MembersData>>

}