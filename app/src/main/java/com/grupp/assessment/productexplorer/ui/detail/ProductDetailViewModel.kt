package com.grupp.assessment.productexplorer.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.data.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepo: ProductRepository
): ViewModel() {

    private val _stateFlow = MutableStateFlow<Result<ProductDetailModel>>(Result.None)
    val stateFlow = _stateFlow.asStateFlow()

    fun fetchDetail(id: String) {
        viewModelScope.launch {
            val product = productRepo.getProductDetail(id)
            _stateFlow.update { Result.Success(product.toDetailModel()) }
        }
    }
}