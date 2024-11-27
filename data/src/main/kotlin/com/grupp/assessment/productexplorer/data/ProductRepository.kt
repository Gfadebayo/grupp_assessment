package com.grupp.assessment.productexplorer.data

import com.grupp.assessment.productexplorer.core.Result
import com.grupp.assessment.productexplorer.data.io.db.LocalDataSource
import com.grupp.assessment.productexplorer.data.io.db.fromProduct
import com.grupp.assessment.productexplorer.data.network.RemoteDataSource
import com.grupp.assessment.productexplorer.domain.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val localSource: LocalDataSource,
    private val remoteSource: RemoteDataSource
) {

    /**
     * Fetches products from the server then saves it into the database.
     * Callers of this method are expected to also call [allProducts] in order to
     * observe changes made to the database in real time including getting the updated list of products
     */
    suspend fun fetchProduct(): Result<Unit> {
        val result = remoteSource.getProducts()

        if(result is Result.Success) {
            localSource.insert(result.data.map { it.fromProduct() })
        }

        return if(result is Result.Success) Result.Success(Unit)
        else result as Result.Error
    }

    suspend fun getProductDetail(id: String): Product {
        return localSource.fetchById(id)
    }

    fun allProducts(): Flow<List<Product>> {
        return localSource.allProducts()
    }
}