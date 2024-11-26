package com.grupp.assessment.productexplorer

import androidx.room.Room
import com.grupp.assessment.productexplorer.data.io.db.ExplorerDatabase
import com.grupp.assessment.productexplorer.data.io.db.LocalDataSource
import com.grupp.assessment.productexplorer.data.io.db.fromProduct
import com.grupp.assessment.productexplorer.domain.Category
import com.grupp.assessment.productexplorer.domain.Product
import com.grupp.assessment.productexplorer.domain.Rating
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import timber.log.Timber
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(RobolectricTestRunner::class)
class LocalDataSourceUnitTest {

    private val db by lazy {
        Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            ExplorerDatabase::class.java,
        ).build()
    }

    private lateinit var localSource: LocalDataSource

    @BeforeTest
    fun init() {
        Timber.plant(TestTree())
        localSource = LocalDataSource(db)
    }

    @AfterTest
    fun clear() {
        db.close()
    }

    @Test
    fun testInsertProduct() {
        runTest {
            val product = dummyProduct.fromProduct()
            localSource.insert(product)

            localSource.insert(product)

            db.query("SELECT * FROM product WHERE title = ? LIMIT 1", arrayOf(product.title)).use {
                val idColumnIndex = it.getColumnIndex("id")

                it.moveToNext()

                assert(it.count > 0)
                assert(it.getString(idColumnIndex) == product.id)
            }
        }
    }

    private val dummyProduct = Product(
        id = "5",
        title = "John Hardy Women's Legends Naga Gold & Silver Dragon Station Chain Bracelet",
        price = "300",
        description = "From our Legends Collection, the Naga was inspired by the mythical water dragon that protects the ocean's pearl. Wear facing inward to be bestowed with love and abundance, or outward for protection.",
        category = Category.JEWELERY,
        imageUrl = "https://fakestoreapi.com/img/71pWzhdJNwL._AC_UL640_QL65_ML3_.jpg",
        rating = Rating(rate = "4.6", count = "900")
    )
}