package io.scal.ambi.model.interactor.base

import io.reactivex.Single
import io.scal.ambi.entity.base.ServerFile
import io.scal.ambi.entity.user.User
import io.scal.ambi.ui.global.picker.FileResource

private const val MAX_FILE_SIZE_IN_PX: Int = 300

interface IFileUploadInteractor {

    fun uploadFile(fileResource: FileResource, maxFileSizeInPx: Int? = MAX_FILE_SIZE_IN_PX): Single<ServerFile>

    fun uploadFile(fileResource: FileResource, creatorUser: User, maxFileSizeInPx: Int? = MAX_FILE_SIZE_IN_PX): Single<ServerFile>
}