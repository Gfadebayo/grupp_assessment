package com.grupp.assessment.productexplorer

import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.data.network.NetworkManager
import com.grupp.assessment.productexplorer.data.network.NullConverter
import com.grupp.assessment.productexplorer.data.network.ProductService
import com.grupp.assessment.productexplorer.data.network.RemoteDataSource
import com.grupp.assessment.productexplorer.data.network.ResultAdapterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.runner.RunWith
import com.grupp.assessment.productexplorer.data.BuildConfig
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(RobolectricTestRunner::class)
class RemoteDataSourceUnitTest {

    lateinit var remoteDataSource: RemoteDataSource

    @BeforeTest
    fun init() {
        Timber.plant(TestTree())

        val client = OkHttpClient.Builder().run {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })

            build()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addCallAdapterFactory(ResultAdapterFactory())
            .addConverterFactory(NullConverter())
//            .addConverterFactory(UnwrapConverter())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ProductService::class.java)

        val networkManager = NetworkManager(RuntimeEnvironment.getApplication())
        remoteDataSource = RemoteDataSource(service, networkManager)
    }

    @Test
    fun testFetchProduct() {
        runTest {
            val result = remoteDataSource.getProducts()
            assert(result is Result.Success && result.data.isNotEmpty())
        }
    }
}