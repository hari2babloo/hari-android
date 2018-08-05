package io.scal.ambi.ui.home.classes

import io.reactivex.Single
import io.scal.ambi.ui.home.classes.about.MembersData

interface IClassesRepository {

    fun loadClasses(page: Long): Single<List<ClassesData>>

    fun getUserDetailsById(userids: Map<String,String>): Single<List<MembersData>>
}