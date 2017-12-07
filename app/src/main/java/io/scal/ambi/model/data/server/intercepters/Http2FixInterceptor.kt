package io.scal.ambi.model.data.server.intercepters

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.net.SocketTimeoutException
import java.util.concurrent.atomic.AtomicReference

class Http2FixInterceptor(private val okHttpClientRef: AtomicReference<OkHttpClient>) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            return chain.proceed(chain.request())
        } catch (e: SocketTimeoutException) {
            // see https://www.bountysource.com/issues/41516259-java-net-sockettimeoutexception-from-http-2-connection-leaves-dead-okhttp-clients-in-pool
            okHttpClientRef.get()?.connectionPool()?.evictAll()
            throw e
        }
    }
}