package io.scal.ambi.di.module

import android.content.Context
import android.support.v7.app.AppCompatDelegate
import com.crashlytics.android.Crashlytics
import com.facebook.drawee.backends.pipeline.DraweeConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.decoder.ImageDecoderConfig
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import dagger.Module
import dagger.Provides
import io.fabric.sdk.android.Fabric
import io.scal.ambi.App
import io.scal.ambi.extensions.FilePathHelper
import io.scal.ambi.extensions.ImageUtils
import io.scal.ambi.extensions.rx.general.RxSchedulers
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
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
        val config = ImageDecoderConfig.newBuilder()
        config.addDecodingCapability(FrescoSvgDecoder.SVG_FORMAT, FrescoSvgDecoder.SvgFormatChecker(), FrescoSvgDecoder.SvgDecoder())
        val configPipline = ImagePipelineConfig.newBuilder(context)
        configPipline.setImageDecoderConfig(config.build())
        configPipline.isDownsampleEnabled = true

        val draweeConfig = DraweeConfig.newBuilder()
        draweeConfig.addCustomDrawableFactory(FrescoSvgDecoder.SvgDrawableFactory(context))

        Fresco.initialize(context, configPipline.build(), draweeConfig.build())
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

    @Singleton
    @Provides
    fun provideAbsScheduler(): RxSchedulersAbs = RxSchedulers()

    @Singleton
    @Provides
    fun provideImageUrils() = ImageUtils(context)

    @Singleton
    @Provides
    fun provideFilePathHelper(imageUtils: ImageUtils) = FilePathHelper(context, imageUtils)
}