package io.scal.ambi.ui.home.classes.about

import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.ui.home.classes.IClassesRepository
import javax.inject.Inject

class AboutInteractor @Inject constructor(private val postsRepository: IClassesRepository,
                                            private val localUserDataRepository: ILocalUserDataRepository) : IAboutInteractor {

    override fun getUserDetailsById(userids: Map<String,String>): Single<List<MembersData>> {
        return postsRepository.getUserDetailsById(userids)
    }

}