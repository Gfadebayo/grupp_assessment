package com.grupp.assessment.productexplorer.data.network

import com.grupp.assessment.productexplorer.data.io.SharedPreferenceManager
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(preferenceManager: SharedPreferenceManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val newRequest = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .build()

        return chain.proceed(newRequest)
    }
}