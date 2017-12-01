package io.scal.ambi.di

import dagger.Component
import io.scal.ambi.App
import io.scal.ambi.di.module.ApiModule
import io.scal.ambi.di.module.AppModule
import io.scal.ambi.di.module.UiCommonModule
import javax.inject.Singleton


@Singleton
@Component(modules = [
    AppModule::class,
    ApiModule::class,
    UiCommonModule::class
])
interface AppComponent {

    fun injectTo(app: App)
}