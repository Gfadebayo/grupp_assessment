package com.grupp.assessment.productexplorer

import android.content.Context
import com.grupp.assessment.productexplorer.core.di.AppModule
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
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestModule {

    @Singleton
    @Provides
    fun provideNetworkManager(@ApplicationContext context: Context): NetworkManager {
        return NetworkManager(context)
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(@ApplicationContext context: Context): LocalDataSource {
        val db = ExplorerDatabase.build(context, true)
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