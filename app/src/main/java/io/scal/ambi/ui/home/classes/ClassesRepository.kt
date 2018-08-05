package io.scal.ambi.ui.home.classes

import io.reactivex.Single
import io.scal.ambi.model.repository.local.LocalUserDataRepository
import io.scal.ambi.ui.home.classes.about.MembersData
import javax.inject.Inject

class ClassesRepository @Inject constructor(private val postsApi: ClassesApi,
                                          private val localUserDataRepository: LocalUserDataRepository) : IClassesRepository {

    override fun getUserDetailsById(userids: Map<String,String>): Single<List<MembersData>> {
        return postsApi.getUserDetailsById(userids).map { it.parse() }
    }

    override fun loadClasses(page: Long): Single<List<ClassesData>> {
        return postsApi.getClasses().map { it.parse() }
    }

}