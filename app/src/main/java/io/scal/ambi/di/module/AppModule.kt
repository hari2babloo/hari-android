package io.scal.ambi.di.module

import android.content.Context
import android.support.v7.app.AppCompatDelegate
import com.crashlytics.android.Crashlytics
import com.facebook.drawee.backends.pipeline.Fresco
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import dagger.Module
import dagger.Provides
import io.fabric.sdk.android.Fabric
import io.scal.ambi.App
import javax.inject.Singleton

@Module
class AppModule(private val context: App) {

    private val refWatcher: RefWatcher

    init {
        // for vector drawables
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // leak detector
        refWatcher = LeakCanary.install(context)

        // analytics setup
        Fabric.with(context, Crashlytics())

        // image loader
        Fresco.initialize(context)
    }

    @Singleton
    @Provides
    fun provideApp(): App = context

    @Singleton
    @Provides
    fun provideContext(): Context = context

    @Singleton
    @Provides
    fun provideRefWatcher(): RefWatcher = refWatcher
}