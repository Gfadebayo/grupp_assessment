package com.grupp.assessment.productexplorer.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.data.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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

            val result = productRepo.fetchProduct()

            _listFlow.update { it.copy(loadState = result) }
        }
    }
}