package com.grupp.assessment.productexplorer.data.io.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.grupp.assessment.productexplorer.domain.Category
import com.grupp.assessment.productexplorer.domain.Product
import com.grupp.assessment.productexplorer.domain.Rating

@Entity(tableName = "product")
data class RoomProduct(
    @PrimaryKey val id: String,

    val title: String = "",

    val price: String = "",

    val category: Category = Category.JEWELERY,

    val description: String = "",

    val imageUrl: String = "",

    val rating: Rating? = null
)

internal class ProductConverter {
    @TypeConverter
    fun fromRating(rating: Rating?): String? {
        return if(rating != null) "${rating.rate}-${rating.count}"
        else null
    }

    @TypeConverter
    fun toRating(value: String?): Rating? {
        return value?.let {
            val split = it.split("-".toRegex())

            Rating(rate = split[0], count = split[1])
        }
    }
}

internal fun Product.fromProduct(): RoomProduct {
    return RoomProduct(
        id = id,
        title = title,
        price = price,
        category = category,
        description = description,
        imageUrl = imageUrl,
        rating = rating
    )
}

internal fun RoomProduct.toProduct(): Product {
    return Product(
        id = id,
        title = title,
        price = price,
        category = category,
        description = description,
        imageUrl = imageUrl,
        rating = rating
    )
}
