package io.scal.ambi.model.data.server.intercepters

import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val localUserDataRepository: ILocalUserDataRepository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request()?.newBuilder()
        localUserDataRepository.getUserInfo()?.let {
            builder?.addHeader("Authorization", "Bearer ${it.token}")
        }
        return chain.proceed(builder!!.build())
    }
}