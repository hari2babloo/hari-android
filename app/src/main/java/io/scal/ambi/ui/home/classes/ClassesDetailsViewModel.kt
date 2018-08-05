package io.scal.ambi.ui.home.classes

import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import javax.inject.Inject
import javax.inject.Named

class ClassesDetailsViewModel @Inject internal constructor(router: BetterRouter, @Named("classesDetails") val classesDetails: ClassesData) : BaseViewModel(router) {

}