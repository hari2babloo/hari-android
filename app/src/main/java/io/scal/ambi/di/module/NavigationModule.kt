package io.scal.ambi.di.module

import dagger.Module
import dagger.Provides
import io.scal.ambi.ui.global.base.BetterRouter
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Named
import javax.inject.Singleton

@Module
class NavigationModule {

    @Singleton
    @Provides
    fun provideApp(): Cicerone<BetterRouter> = Cicerone.create(BetterRouter())

    @Named("rootNavigationHolder")
    @Singleton
    @Provides
    fun getNavigationHolder(cicerone: Cicerone<BetterRouter>): NavigatorHolder {
        return cicerone.navigatorHolder
    }

    @Named("rootRouter")
    @Singleton
    @Provides
    fun getRouter(cicerone: Cicerone<BetterRouter>): BetterRouter {
        return cicerone.router
    }
}