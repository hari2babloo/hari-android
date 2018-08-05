package io.scal.ambi.ui.home.classes

import io.reactivex.Single
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class ClassesInteractor @Inject constructor(private val postsRepository: IClassesRepository,
                                            private val localUserDataRepository: ILocalUserDataRepository) : IClassesInteractor {
    override fun loadClasses(page: Int): Single<List<ClassesData>> =
            postsRepository.loadClasses(page.toLong() - 1)

}