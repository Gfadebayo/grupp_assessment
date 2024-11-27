package com.grupp.assessment.productexplorer.ui.list.mapper

import com.grupp.assessment.productexplorer.domain.Product
import com.grupp.assessment.productexplorer.ui.utils.formatAsAmount

data class ListUi(
    val id: String,
    val image: String,
    val title: String,
    val price: String,
    val rating: String
)

fun Product.toListUi(): ListUi {
    return ListUi(
        id = id,
        image = imageUrl,
        title = title,
        price = price.formatAsAmount(),
        rating = rating?.let { "${it.rate} (${it.count})" } ?: ""
    )
}

fun List<Product>.toListUiList(): List<ListUi> {
    return map { it.toListUi() }
}