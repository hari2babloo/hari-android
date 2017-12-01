package io.scal.ambi

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import io.scal.ambi.di.AppComponent
import io.scal.ambi.di.DaggerAppComponent
import io.scal.ambi.di.module.AppModule
import timber.log.Timber

class App : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }

        initLogger()
        initDi()
    }


    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initDi() {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
        appComponent.injectTo(this)
    }
}