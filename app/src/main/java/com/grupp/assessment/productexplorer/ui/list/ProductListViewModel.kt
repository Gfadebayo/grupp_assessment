package com.grupp.assessment.productexplorer.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.core.transform
import com.grupp.assessment.productexplorer.data.ProductRepository
import com.grupp.assessment.productexplorer.data.io.db.LocalDataSource
import com.grupp.assessment.productexplorer.data.network.RemoteDataSource
import com.grupp.assessment.productexplorer.domain.Product
import com.grupp.assessment.productexplorer.ui.list.mapper.ListUi
import com.grupp.assessment.productexplorer.ui.list.mapper.toListUiList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ProductListState(
    val loadState: Result<Unit> = Result.None,
    val products: List<ListUi> = emptyList(),
)

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepo: ProductRepository,
): ViewModel() {

    private val _listFlow = MutableStateFlow(ProductListState())
    val listFlow = _listFlow.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            productRepo.allProducts()
                .collectLatest { products ->
                    Timber.d("Calling allproducts with size: ${products.size}")
                    _listFlow.update {
                        it.copy(
                            products = products.toListUiList(),
                        )
                    }
                }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _listFlow.update { it.copy(loadState = Result.Loading) }

            val result = productRepo.fetchProduct(1)

            _listFlow.update { it.copy(loadState = result) }
        }
    }
}