package io.scal.ambi.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import io.scal.ambi.App
import io.scal.ambi.di.module.builders.ActivityBuilder
import io.scal.ambi.di.module.ApiModule
import io.scal.ambi.di.module.AppModule
import io.scal.ambi.di.module.UiCommonModule
import javax.inject.Singleton


@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    ActivityBuilder::class,
    AppModule::class,
    ApiModule::class,
    UiCommonModule::class
])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun appModule(appModule: AppModule): Builder

        fun build(): AppComponent
    }

    fun injectTo(app: App)
}