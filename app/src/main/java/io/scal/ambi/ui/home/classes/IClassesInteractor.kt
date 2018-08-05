package io.scal.ambi.ui.home.classes

import io.reactivex.Single

/**
 * Created by chandra on 30-07-2018.
 */
interface IClassesInteractor {

    fun loadClasses(page: Int): Single<List<ClassesData>>

}