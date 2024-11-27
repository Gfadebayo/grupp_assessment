package com.grupp.assessment.productexplorer

import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.data.ProductRepository
import com.grupp.assessment.productexplorer.ui.detail.ProductDetailViewModel
import com.grupp.assessment.productexplorer.ui.list.ProductListViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
class ProductDetailViewModelUnitTest {

    @get:Rule
    val rule = HiltAndroidRule(this)

    @Inject
    lateinit var repo: ProductRepository

    val viewModel: ProductDetailViewModel by lazy { ProductDetailViewModel(repo) }

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
    fun testFetchingDetail() {
        runTest(timeout = 1.minutes) {
            //Put data into db
            repo.fetchProduct()

            viewModel.fetchDetail("1")

            val detail = viewModel.stateFlow.first { it !is Result.None }

            assert(detail is Result.Success)
        }
    }
}