package io.scal.ambi.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import io.scal.ambi.App
import io.scal.ambi.di.module.*
import io.scal.ambi.di.module.builders.ActivityBuilder
import io.scal.ambi.di.module.builders.FragmentBuilder
import javax.inject.Singleton


@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ActivityBuilder::class,
    FragmentBuilder::class,
    AppModule::class,
    ApiModule::class,
    RepositoryModule::class,
    UiCommonModule::class,
    NavigationModule::class
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