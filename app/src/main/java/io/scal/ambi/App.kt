package io.scal.ambi

import android.app.Activity
import android.support.multidex.MultiDexApplication
import com.ambi.work.BuildConfig
import com.squareup.leakcanary.LeakCanary
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.scal.ambi.di.AppComponent
import io.scal.ambi.di.DaggerAppComponent
import io.scal.ambi.di.module.AppModule
import io.scal.ambi.model.repository.data.chat.IChatRepository
import io.scal.ambi.model.repository.local.ILocalDataRepository
import timber.log.Timber
import javax.inject.Inject


class App : MultiDexApplication(), HasActivityInjector {

    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var localDataRepository: ILocalDataRepository

    @Inject
    lateinit var chatRepository: IChatRepository

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }

        initLogger()
        initDi()

        EmojiManager.install(GoogleEmojiProvider()) // todo move to other place if this lib is ok

        chatRepository.listenForPushToken()
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity> {
        return activityDispatchingAndroidInjector
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initDi() {
        DaggerAppComponent
            .builder()
            .application(this)
            .appModule(AppModule(this))
            .build()
            .injectTo(this)
    }
}