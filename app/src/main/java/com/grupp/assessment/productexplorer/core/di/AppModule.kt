package com.grupp.assessment.productexplorer.core.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.grupp.assessment.productexplorer.data.io.SharedPreferenceManager
import com.grupp.assessment.productexplorer.data.network.ExplorerService
import com.grupp.assessment.productexplorer.data.network.HeaderInterceptor
import com.grupp.assessment.productexplorer.BuildConfig
import com.grupp.assessment.productexplorer.core.Constants
import com.grupp.assessment.productexplorer.data.io.db.ExplorerDatabase
import com.grupp.assessment.productexplorer.data.io.db.LocalDataSource
import com.grupp.assessment.productexplorer.data.network.NullConverter
import com.grupp.assessment.productexplorer.data.network.RemoteDataSource
import com.grupp.assessment.productexplorer.data.network.ResultAdapterFactory
import com.grupp.assessment.productexplorer.data.network.UnwrapConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        prefManager: SharedPreferenceManager
    ): OkHttpClient = OkHttpClient.Builder().run {
        connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
        writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
        readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)

        cache(null)

        addInterceptor(HeaderInterceptor(prefManager))

        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })

            addInterceptor(ChuckerInterceptor.Builder(context)
                    .collector(ChuckerCollector(context))
                    .maxContentLength(250000L)
                    .redactHeaders(emptySet())
                    .alwaysReadResponseBody(false)
                    .build())
        }

        build()
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(@ApplicationContext context: Context): LocalDataSource {
        val db = ExplorerDatabase.build(context)
        return LocalDataSource(db)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        client: OkHttpClient
    ): RemoteDataSource {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addCallAdapterFactory(ResultAdapterFactory())
            .addConverterFactory(NullConverter())
            .addConverterFactory(UnwrapConverter())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ExplorerService::class.java)

        return RemoteDataSource(service)
    }
}