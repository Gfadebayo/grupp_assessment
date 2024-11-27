package com.grupp.assessment.productexplorer.domain

import com.google.gson.annotations.SerializedName

enum class Category {
    @SerializedName("jewelery") JEWELERY,
    @SerializedName("men's clothing") MENS_CLOTHING,
    @SerializedName("women's clothing") WOMENS_CLOTHING,
    @SerializedName("electronics") ELECTRONICS
}

data class Product(
    @SerializedName("id") val id: String = "",

    @SerializedName("title") val title: String = "",

    @SerializedName("price") val price: String = "",

    @SerializedName("category") val category: Category = Category.JEWELERY,

    @SerializedName("description") val description: String = "",

    @SerializedName("image") val imageUrl: String = "",

    @SerializedName("rating") val rating: Rating? = null
)

data class Rating(
    @SerializedName("rate") val rate: String = "",

    @SerializedName("count") val count: String = ""
)
