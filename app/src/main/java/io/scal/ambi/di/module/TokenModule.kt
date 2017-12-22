package io.scal.ambi.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Module
class TokenModule {

    @Named("twilioAccessToken")
    @Provides
    fun provideTwilioAccessToken(context: Context): String {
        return "TODO" // todo change with actual
    }
}