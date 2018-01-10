package io.scal.ambi.di.module

import dagger.Module
import dagger.Provides
import io.scal.ambi.BuildConfig
import io.scal.ambi.model.data.server.AuthApi
import io.scal.ambi.model.data.server.ChatApi
import io.scal.ambi.model.data.server.PostsApi
import io.scal.ambi.model.data.server.UserApi
import io.scal.ambi.model.data.server.intercepters.AuthInterceptor
import io.scal.ambi.model.data.server.intercepters.Http2FixInterceptor
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Module
class ApiModule {

    @Singleton
    @Provides
    internal fun provideOkHttpClient(localUserDataRepository: ILocalUserDataRepository): OkHttpClient {
        val okHttpReference = AtomicReference<OkHttpClient>()
        val okHttpClientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(AuthInterceptor(localUserDataRepository))
            okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            okHttpClientBuilder.addInterceptor(Http2FixInterceptor(okHttpReference))
        }
        okHttpClientBuilder.connectTimeout(10, TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(60, TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(60, TimeUnit.SECONDS)
        val okHttp = okHttpClientBuilder.build()
        okHttpReference.set(okHttp)
        return okHttp
    }

    @Singleton
    @Provides
    internal fun provideRetrofitBuilder(okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
    }

    @Singleton
    @Named("mainServer")
    @Provides
    internal fun provideRetrofit(retrofitBuilder: Retrofit.Builder): Retrofit {
        return retrofitBuilder
            .baseUrl(BuildConfig.MAIN_SERVER_URL)
            .build()
    }

    @Provides
    @Singleton
    internal fun provideAuthApi(@Named("mainServer") retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    internal fun provideStudentApi(@Named("mainServer") retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    internal fun providePostsApi(@Named("mainServer") retrofit: Retrofit): PostsApi =
        retrofit.create(PostsApi::class.java)

    @Provides
    @Singleton
    internal fun provideChatApi(@Named("mainServer") retrofit: Retrofit): ChatApi =
        retrofit.create(ChatApi::class.java)
}