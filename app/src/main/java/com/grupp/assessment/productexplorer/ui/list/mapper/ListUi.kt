package com.grupp.assessment.productexplorer.ui.list.mapper

import com.grupp.assessment.productexplorer.domain.Product

data class ListUi(
    val id: String,
    val image: String,
    val title: String,
    val price: String,
    val rating: String
)

fun com.grupp.assessment.productexplorer.domain.Product.toListUi(): ListUi {
    return ListUi(
        id = id,
        image = imageUrl,
        title = title,
        price = price,
        rating = rating?.let { "${it.rate} (${it.count})" } ?: ""
    )
}

fun List<com.grupp.assessment.productexplorer.domain.Product>.toListUiList(): List<ListUi> {
    return map { it.toListUi() }
}