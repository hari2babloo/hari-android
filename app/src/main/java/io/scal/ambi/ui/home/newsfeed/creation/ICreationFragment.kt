package io.scal.ambi.ui.home.newsfeed.creation

import io.scal.ambi.ui.global.picker.FileResource

interface ICreationFragment {

    fun setPickedFile(fileResource: FileResource, image: Boolean)
}