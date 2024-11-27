package com.grupp.assessment.productexplorer.ui.list

import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.domain.Product
import com.grupp.assessment.productexplorer.ui.utils.formatAsAmount

data class ProductListState(
    val loadState: Result<Unit> = Result.None,
    val products: List<ProductItem> = emptyList(),
)

data class ProductItem(
    val id: String,
    val image: String,
    val title: String,
    val price: String,
    val rating: String
)

fun Product.toListUi(): ProductItem {
    return ProductItem(
        id = id,
        image = imageUrl,
        title = title,
        price = price.formatAsAmount(),
        rating = rating?.let { "${it.rate} (${it.count})" } ?: ""
    )
}

fun List<Product>.toListUiList(): List<ProductItem> {
    return map { it.toListUi() }
}