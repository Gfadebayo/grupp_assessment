package com.grupp.assessment.productexplorer

import androidx.room.Room
import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.data.ProductRepository
import com.grupp.assessment.productexplorer.data.io.db.ExplorerDatabase
import com.grupp.assessment.productexplorer.data.io.db.LocalDataSource
import com.grupp.assessment.productexplorer.data.network.NetworkManager
import com.grupp.assessment.productexplorer.data.network.RemoteDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import timber.log.Timber
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(RobolectricTestRunner::class)
class ProductRepositoryUnitTest {

    private lateinit var repo: ProductRepository

    private val db by lazy {
        Room.inMemoryDatabaseBuilder(
            context = RuntimeEnvironment.getApplication(),
            klass = ExplorerDatabase::class.java
        ).build()
    }

    @BeforeTest
    fun init() {
        Timber.plant(TestTree())

        val localSource = LocalDataSource(db)

        val networkManager = NetworkManager(RuntimeEnvironment.getApplication())

        val remoteSource = RemoteDataSource.build(networkManager)

        repo = ProductRepository(localSource, remoteSource)
    }

    @AfterTest
    fun close() {
        db.close()
    }

    @Test
    fun test() {
        runTest {
            val result = repo.fetchProduct(1)

            assert(result is Result.Success)

            val firstProductList = repo.allProducts().first()

            assert(firstProductList.isNotEmpty())
        }
    }
}