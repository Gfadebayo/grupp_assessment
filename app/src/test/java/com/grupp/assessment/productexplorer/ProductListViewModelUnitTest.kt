package com.grupp.assessment.productexplorer

import android.util.Log
import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.data.ProductRepository
import com.grupp.assessment.productexplorer.ui.list.ProductListViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import timber.log.Timber
import javax.inject.Inject

@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
class ProductListViewModelUnitTest {

    @get:Rule
    val rule = HiltAndroidRule(this)

    @Inject
    lateinit var repo: ProductRepository

    val viewModel: ProductListViewModel by lazy { ProductListViewModel(repo) }

    @Before
    fun init() {
        rule.inject()
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testFetchProducts() {
        runTest {
            viewModel.fetchProducts()

            val productState = viewModel.listFlow.first {
                it.loadState is Result.Success && it.products.isNotEmpty()
            }

            assert(productState.loadState is Result.Success)

            assert(productState.products.isNotEmpty())
        }
    }
}