package com.grupp.assessment.productexplorer.data.network

import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.domain.Product
import retrofit2.http.GET

interface ProductService {

    @GET("products?limit=5&sort=desc")
    suspend fun productList(): Result<List<Product>>
}