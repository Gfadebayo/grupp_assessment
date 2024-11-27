package com.grupp.assessment.productexplorer.data.network

import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.domain.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import com.grupp.assessment.productexplorer.data.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RemoteDataSource(
    private val service: ProductService,
    private val networkManager: NetworkManager
) {
    companion object {
        fun build(networkManager: NetworkManager): RemoteDataSource {
            val client = OkHttpClient.Builder().run {
                connectTimeout(5, TimeUnit.SECONDS)
                writeTimeout(5, TimeUnit.SECONDS)
                readTimeout(5, TimeUnit.SECONDS)

                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }

                build()
            }

            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addCallAdapterFactory(ResultAdapterFactory())
                .addConverterFactory(NullConverter())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(ProductService::class.java)

            return RemoteDataSource(service, networkManager)
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    private var isOnline = networkManager.isOnline.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = true
    )

    suspend fun getProducts(): Result<List<Product>> {
        return send { service.productList() }
    }

    private suspend fun<T> send(command: suspend () -> Result<T>): Result<T> {
        return if(isOnline.value) withContext(Dispatchers.IO) { command() }
        else Result.NetworkError
    }
}