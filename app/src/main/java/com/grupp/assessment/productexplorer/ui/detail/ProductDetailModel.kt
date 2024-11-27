package com.grupp.assessment.productexplorer.ui.detail

import android.content.Context
import com.grupp.assessment.productexplorer.R
import com.grupp.assessment.productexplorer.domain.Category
import com.grupp.assessment.productexplorer.domain.Product

data class ProductDetailModel(
    val imageUrl: String,
    val category: Category,
    val rating: String,
    val description: String,
    val price: String,
    val title: String
)

fun com.grupp.assessment.productexplorer.domain.Product.toDetailModel(): ProductDetailModel {
    return ProductDetailModel(
        imageUrl = imageUrl,
        category = category,
        rating = rating?.let { "${it.rate} (${it.count})" } ?: "",
        description = description,
        price = price,
        title = title
    )
}

fun Category.toDetailCategory(context: Context): String {
    val resId = when(this) {
        Category.JEWELERY -> R.string.jewellery
        Category.MENS_CLOTHING -> R.string.mens_clothing
        Category.WOMENS_CLOTHING -> R.string.womens_clothing
        Category.ELECTRONICS -> R.string.electronics
    }

    return context.getString(resId)
}
