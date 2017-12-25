package io.scal.ambi.ui.global.picker

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey

@Module
internal abstract class PickerModule {

    @Binds
    @IntoMap
    @ViewModelKey(PickerViewModel::class)
    abstract fun bindViewModel(pickerViewModel: PickerViewModel): ViewModel
}