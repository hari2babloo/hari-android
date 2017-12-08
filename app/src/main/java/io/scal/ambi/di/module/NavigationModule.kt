package io.scal.ambi.di.module

import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Named
import javax.inject.Singleton

@Module
class NavigationModule {

    @Singleton
    @Provides
    fun provideApp(): Cicerone<Router> = Cicerone.create()

    @Named("rootNavigationHolder")
    @Singleton
    @Provides
    fun getNavigationHolder(cicerone: Cicerone<Router>): NavigatorHolder {
        return cicerone.navigatorHolder
    }

    @Named("rootRouter")
    @Singleton
    @Provides
    fun getRouter(cicerone: Cicerone<Router>): Router {
        return cicerone.router
    }
}