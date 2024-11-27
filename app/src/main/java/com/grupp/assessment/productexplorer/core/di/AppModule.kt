package com.grupp.assessment.productexplorer.core.di

import android.content.Context
import com.grupp.assessment.productexplorer.data.ProductRepository
import com.grupp.assessment.productexplorer.data.io.db.ExplorerDatabase
import com.grupp.assessment.productexplorer.data.io.db.LocalDataSource
import com.grupp.assessment.productexplorer.data.network.NetworkManager
import com.grupp.assessment.productexplorer.data.network.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideNetworkManager(@ApplicationContext context: Context): NetworkManager {
        return NetworkManager(context)
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
        networkManager: NetworkManager
    ): RemoteDataSource {
        return RemoteDataSource.build(networkManager)
    }

    @Provides
    @Singleton
    fun provideProductRepo(
        localSource: LocalDataSource,
        remoteSource: RemoteDataSource
    ): ProductRepository {
        return ProductRepository(
            localSource = localSource,
            remoteSource = remoteSource
        )
    }
}